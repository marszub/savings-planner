import { InputAdornment, TextField } from "@mui/material";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { useState } from "react";
import { BalanceForm } from "../../models/balance-form";
import { moneyValidators } from "../../utils/money-validators";
import { moneyFormatter } from "../../utils/money-formatter";

export default function BalanceField() {
    const [balanceErrorMessage, setBalanceErrorMessage] = useState("");

    const defaultBalance = "0.00";

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new BalanceForm(
            data.get('balance')
        );

        const balanceError = moneyValidators.validateAmount(formModel.balance);
        setBalanceErrorMessage(balanceError);
        if (balanceError) {
            return;
        }

        const balance = moneyFormatter.mapStringToPenniesNumber(formModel.balance);
        event.currentTarget.balance.value = moneyFormatter.mapPenniesNumberToString(balance);
    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 2 }}>
            <TextField
                label="Account Balance"
                id="balance"
                name="balance"
                defaultValue={defaultBalance}
                sx={{ m: 1, width: '26ch' }}
                InputProps={{
                    endAdornment: <InputAdornment position="end">PLN</InputAdornment>,
                }}
                error={!!balanceErrorMessage}
                helperText={balanceErrorMessage}
            />
            <Button type="submit" variant="contained" size="large" sx={{ mt: 1, height: '6.7ch' }}>Update</Button>
        </Box>
    );
}
