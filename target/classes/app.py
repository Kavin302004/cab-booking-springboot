from flask import Flask, request, jsonify
from datetime import datetime, timedelta
import spacy
import re

app = Flask(__name__)
nlp = spacy.load("en_core_web_sm")  # Load English NLP model

def parse_time(text):
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
        
    
    return time.time()

def parse_datetime(text):
    doc = nlp(text.lower())
    today = datetime.now().date()
    
    # Check for explicit dates first (YYYY-MM-DD)
    explicit_date = re.search(r'\b(\d{4}-\d{2}-\d{2})\b', text)
    if explicit_date:
        date = datetime.strptime(explicit_date.group(1), '%Y-%m-%d').date()
    else:
        # Fallback to relative dates (today/tomorrow)
        date = today + timedelta(days=1)  # Default to tomorrow
    
    # Time parsing (existing implementation)
    time = parse_time(text)  
    
    return date, time

@app.route('/parse', methods=['POST'])
def parse():
    try:
        text = request.json['text'].lower()
        doc = nlp(text)
        
        # Improved location extraction
        locations = [ent.text for ent in doc.ents if ent.label_ == "GPE" or ent.label_ == "LOC"]
        pickup = locations[0] if len(locations) > 0 else "Unknown Location"
        drop = locations[1] if len(locations) > 1 else "Unknown Destination"
        
        # Date/time parsing (existing implementation)
        date, time = parse_datetime(text)
        
        return jsonify({
            "pickupLocation": pickup,
            "dropLocation": drop,  # Now guaranteed non-null
            "LocalDate": date.strftime('%Y-%m-%d'),
            "LocalTime": time.strftime('%H:%M')
        })
        
    except Exception as e:
        return jsonify({
            "pickupLocation": "Unknown",
            "dropLocation": "Unknown",
            "LocalDate": (datetime.now() + timedelta(days=1)).strftime('%Y-%m-%d'),
            "LocalTime": "10:00"
        })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)