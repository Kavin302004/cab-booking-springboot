from flask import Flask, request, jsonify
from datetime import datetime, timedelta
import spacy
import re

app = Flask(__name__)
nlp = spacy.load("en_core_web_sm")  # Load English NLP model

def parse_datetime(text):
    """Extract date/time using NLP rules"""
    doc = nlp(text.lower())
    
    # Default to now + 1 day if no date found
    date = datetime.now() + timedelta(days=1)  
    time = datetime.now().replace(hour=10, minute=0)  # Default 10 AM
    
    for ent in doc.ents:
        if ent.label_ == "TIME":
            if "pm" in ent.text:
                time = time.replace(hour=int(re.search(r'\d+', ent.text).group()) + 12)
            else:
                time = time.replace(hour=int(re.search(r'\d+', ent.text).group()))
        elif ent.label_ == "DATE":
            if "today" in ent.text:
                date = datetime.now()
            elif "tomorrow" in ent.text:
                date = datetime.now() + timedelta(days=1)
    
    return date.date(), time.time()

@app.route('/parse', methods=['POST'])
def parse():
    try:
        text = request.json['text']
        doc = nlp(text)
        
        # Extract locations (improved pattern)
        locations = [ent.text for ent in doc.ents if ent.label_ == "GPE"]
        pickup = locations[0] if len(locations) > 0 else None
        drop = locations[1] if len(locations) > 1 else None
        
        # Parse date/time
        date, time = parse_datetime(text)
        
        return jsonify({
            "pickupLocation": pickup,
            "dropLocation": drop,
            "date": date.strftime('%Y-%m-%d'),
            "time": time.strftime('%H:%M')
        })
        
    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)