import * as React from "react";
import Typography from "@mui/material/Typography";

export default function Footer(props) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Made with ❤️・Enchanted by 🦄'}
        </Typography>
    );
}
