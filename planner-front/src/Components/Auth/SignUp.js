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
import { SignUpForm } from "../../models/sign-up-form";
import { userValidators } from "../../utils/user-validators";
import { userService } from "../../services/user-service";
import { HTTP_BAD_REQUEST, HTTP_CONFLICT, HTTP_OK } from "../../utils/http-status";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const theme = createTheme();

export default function SignUp() {
    const navigate = useNavigate();

    const [nickErrorMessage, setNickErrorMessage] = useState("");
    const [isNickError, setIsNickError] = useState(false);

    const [emailErrorMessage, setEmailErrorMessage] = useState("");
    const [isEmailError, setIsEmailError] = useState(false);

    const [passwordErrorMessage, setPasswordErrorMessage] = useState("");
    const [isPasswordError, setIsPasswordError] = useState(false);

    const [repeatPasswordErrorMessage, setRepeatPasswordErrorMessage] = useState("");
    const [isRepeatPasswordError, setIsRepeatPasswordError] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new SignUpForm(
            data.get('nick'),
            data.get('email'),
            data.get('password'),
            data.get('repeatPassword')
        );

        let nickError = userValidators.validateNick(formModel.nick);
        setNickErrorMessage(nickError);
        setIsNickError(!!nickError);
        if (nickError) console.log(nickError);

        let emailError = userValidators.validateEmail(formModel.email);
        setEmailErrorMessage(emailError);
        setIsEmailError(!!emailError);
        if (emailError) console.log(emailError);

        let passwordError = userValidators.validatePassword(formModel.password);
        setPasswordErrorMessage(passwordError);
        setIsPasswordError(!!passwordError);
        if (passwordError) console.log(passwordError);

        let repeatPasswordError = userValidators.validatePassword(formModel.repeatPassword);
        setRepeatPasswordErrorMessage(repeatPasswordError);
        setIsRepeatPasswordError(!!repeatPasswordError);
        if (repeatPasswordError) console.log(repeatPasswordError);

        let passwordMatchError = "";
        if (!(passwordError || repeatPasswordError)) {
            passwordMatchError = userValidators.validatePasswordsMatch(formModel.repeatPassword, formModel.password);
            setPasswordErrorMessage(passwordMatchError);
            setIsPasswordError(!!passwordMatchError);
            setRepeatPasswordErrorMessage(passwordMatchError);
            setIsRepeatPasswordError(!!passwordMatchError);
            if (passwordMatchError) console.log(passwordMatchError);
        }

        if (nickError || emailError || passwordError || repeatPasswordError || passwordMatchError) {
            return;
        }

        userService.signUp(formModel)
            .then(res => {
                switch (res.status) {
                    case HTTP_OK:
                        console.log("User has been created");
                        navigate("/");
                        break;
                    case HTTP_BAD_REQUEST:
                        console.log("Invalid request body");
                        navigate("/error");
                        break;
                    case HTTP_CONFLICT:
                        let error = "Email or nick already taken";
                        console.log(error);
                        setNickErrorMessage(error);
                        setIsNickError(true);
                        setEmailErrorMessage(error);
                        setIsEmailError(true);
                        break;
                    default:
                        console.log("Unexpected error")
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
                        Sign up
                    </Typography>
                    <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    id="nick"
                                    label="Nickname"
                                    name="nick"
                                    autoComplete="nickname"
                                    error={isNickError}
                                    helperText={nickErrorMessage}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    id="email"
                                    label="Email Address"
                                    name="email"
                                    autoComplete="email"
                                    error={isEmailError}
                                    helperText={emailErrorMessage}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="password"
                                    label="Password"
                                    type="password"
                                    id="password"
                                    autoComplete="new-password"
                                    error={isPasswordError}
                                    helperText={passwordErrorMessage}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="repeatPassword"
                                    label="Repeat Password"
                                    type="password"
                                    id="repeatPassword"
                                    autoComplete="new-password"
                                    error={isRepeatPasswordError}
                                    helperText={repeatPasswordErrorMessage}
                                />
                            </Grid>
                        </Grid>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Sign Up
                        </Button>
                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link onClick={() => navigate("/sign-in")} href="" variant="body2">
                                    Already have an account? Sign in
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}
