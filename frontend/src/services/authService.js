const BASE_URL = 'http://localhost:8080/api/auth';

export const authService = {
    login : async (email,password) => {
        try {
            const response = await fetch (`${BASE_URL}/login`,{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({email,password}),
            });
            if(!response.ok) {
                throw new Error('Invalid Credentials');
            } 
            const token = await response.text();
            localStorage.setItem('token',token);
            return token;
        }
        catch(error) {
            throw Error(error.message || 'Login Failed');
        }
    },
    logout: () => {
        localStorage.removeItem('token');
    },

    isAuthenticated: ()=> {
        return !!localStorage.getItem('token');
    }
};