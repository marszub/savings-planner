import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import * as React from "react";
import Link from "@mui/material/Link";
import { useNavigate } from "react-router-dom";

const theme = createTheme();

export default function NotFoundPage() {
    const navigate = useNavigate();

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="s">
                <CssBaseline />
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Typography component="h1" variant="h3">
                        404
                    </Typography>
                    <Typography component="h1" variant="h4">
                        Ooops! You weren't supposed to see this
                    </Typography>
                    <Typography component="h2" variant="h6">
                        You may have mistyped the address or the page may have moved.
                    </Typography>
                    <Typography component="h2" variant="h6">
                        Return to <Link onClick={() => navigate("/")} href="">home page</Link> and remember: you haven't seen anything
                    </Typography>
                </Box>
            </Container>
        </ThemeProvider>
    );
}
