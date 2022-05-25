import * as React from "react";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import {useSearchParams} from "react-router-dom";

const theme = createTheme();

export default function ErrorPage() {
    const [searchParams, setSearchParams] = useSearchParams();

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
                    <Typography component="h1" variant="h4">
                        Aaaah! Something went wrong
                    </Typography>
                    <Typography component="h2" variant="h6">
                        Brace yourself till we get the error fixed.
                    </Typography>
                    <Typography component="h2" variant="h6">
                        You may also refresh the page or try again later
                    </Typography>
                  { searchParams.get('text') &&
                      <Typography
                          component="h2"
                          variant="h5"
                          sx={{
                            marginTop: 8
                          }}
                      >
                        { searchParams.get('text') }
                      </Typography>
                  }
                </Box>
            </Container>
        </ThemeProvider>
    );
}
