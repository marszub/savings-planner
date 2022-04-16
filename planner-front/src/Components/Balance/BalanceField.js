import { InputAdornment, TextField } from "@mui/material";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { useState } from "react";
import { BalanceForm } from "../../models/balance-form";
import { moneyValidators } from "../../utils/money-validators";
import { moneyFormatter } from "../../utils/money-formatter";

export default function BalanceField() {
    const [amountErrorMessage, setAmountErrorMessage] = useState("");

    const defaultAmount = "0.00";

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new BalanceForm(
            data.get('amount')
        );

        const amountError = moneyValidators.validateAmount(formModel.amount);
        setAmountErrorMessage(amountError);
        if (amountError) {
            return;
        }

        const amount = moneyFormatter.mapStringToPenniesNumber(formModel.amount);
        event.currentTarget.amount.value = moneyFormatter.mapPenniesNumberToString(amount);
    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 2 }}>
            <TextField
                label="Account Balance"
                id="amount"
                name="amount"
                defaultValue={defaultAmount}
                sx={{ m: 1, width: '26ch' }}
                InputProps={{
                    endAdornment: <InputAdornment position="end">PLN</InputAdornment>,
                }}
                error={!!amountErrorMessage}
                helperText={amountErrorMessage}
            />
            <Button type="submit" variant="contained" size="large" sx={{ mt: 1, height: '6.7ch' }}>Update</Button>
        </Box>
    );
}
