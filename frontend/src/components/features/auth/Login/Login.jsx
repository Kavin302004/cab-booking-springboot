import React,{useState} from 'react';

import {
    Box,
    Paper,
    TextField,
    Button,
    Typography,
    Alert,
    IconButton,
    InputAdornment,
    Container
} from '@mui/material';

import  {Password, Visibility,VisibilityOff} from '@mui/icons-material';
import {useNavigate } from 'react-router-dom';
import { authService} from '../../../../services/authService';

const Login=() => {
    const [formData,setFormData]=useState({
        email:'',
        password:''
    });

    const [error,setError]=useState('');

    const [loading,setLoading]=useState(false);

    const [showPassword,setShowPassword]=useState(false);

    const navigate=useNavigate();

    const handleChange = (e)=> {
        const {name,value}=e.target;

        setFormData(prev => ({
            ...prev,
            [name]:value
        }));

    };

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop:8,
                    display:'flex',
                    flexDirection:'column',
                    alignItems:'center',
                }}
            ></Box>

        </Container>
    )


}