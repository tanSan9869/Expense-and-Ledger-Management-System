/* eslint-disable no-unused-vars */
import React, { createContext, useEffect, useState } from 'react'

const AuthContext = createContext(null);    

export const AuthProvider = ({children}) =>{
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      
        const savedToken = localStorage.getItem("token")
        const savedUser = localStorage.getItem("user")

        if(savedToken && savedUser){
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setToken(savedToken);
            setUser(JSON.parse(savedUser));
        }
        setLoading(false);
    }, [])
    

    const login = (authData) =>{
        setToken(authData.token);
        setUser({
            userId:authData.userId,
            username:authData.username,
            email:authData.email
        });
        localStorage.setItem("token",authData.token);
        localStorage.setItem("user",JSON.stringify({
            userId: authData.userId,
            username: authData.username,
            email: authData.email
        }));
    };

    const logout = () =>{
        setToken(null);
        setUser(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    };
    
    return (
        <AuthContext.Provider value={{user,token,login,logout,loading}}>
            {children}
        </AuthContext.Provider>
    );
}


export default AuthContext;