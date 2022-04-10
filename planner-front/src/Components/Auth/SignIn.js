import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { userValidators } from "../../utils/user-validators";
import { userService } from "../../services/user-service";
import { HTTP_BAD_REQUEST, HTTP_OK, HTTP_UNAUTHORIZED } from "../../utils/http-status";
import { SignInForm } from "../../models/sign-in-form";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const theme = createTheme();

export default function SignIn() {
    const navigate = useNavigate();

    const [loginErrorMessage, setLoginErrorMessage] = useState("");
    const [isLoginError, setIsLoginError] = useState(false);

    const [passwordErrorMessage, setPasswordErrorMessage] = useState("");
    const [isPasswordError, setIsPasswordError] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new SignInForm(
            data.get('login'),
            data.get('password')
        );

        let loginError = userValidators.validateLogin(formModel.login);
        setLoginErrorMessage(loginError);
        setIsLoginError(!!loginError);
        if (loginError) console.log(loginError);

        let passwordError = userValidators.validatePassword(formModel.password);
        setPasswordErrorMessage(passwordError);
        setIsPasswordError(!!passwordError);
        if (passwordError) console.log(passwordError);

        if (loginError || passwordError) {
            return;
        }

        userService.signIn(formModel)
            .then(res => {
                switch (res.status) {
                    case HTTP_OK:
                        console.log("Login successful");
                        navigate("/");
                        break;
                    case HTTP_BAD_REQUEST:
                        console.log("Invalid request body");
                        navigate("/error");
                        break;
                    case HTTP_UNAUTHORIZED:
                        let error = "Wrong login or password";
                        console.log(error);
                        setLoginErrorMessage(error);
                        setIsLoginError(true);
                        setPasswordErrorMessage(error);
                        setIsPasswordError(true);
                        break;
                    default:
                        console.log("Unexpected error");
                        navigate("/error");
                }
            })
            .catch(err => {
                console.log(err);
                navigate("/error");
        })
    };

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline />
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                        <LockOutlinedIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Sign in
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="login"
                            label="Login"
                            name="login"
                            autoComplete="nickname"
                            autoFocus
                            error={isLoginError}
                            helperText={loginErrorMessage}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                            autoComplete="current-password"
                            error={isPasswordError}
                            helperText={passwordErrorMessage}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Sign In
                        </Button>
                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link onClick={() => navigate("/sign-up")} href="#" variant="body2">
                                    Don't have an account? Sign Up
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}
