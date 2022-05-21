import { InputAdornment, TextField } from "@mui/material";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { BalanceForm } from "../../models/balance-form";
import { moneyValidators } from "../../utils/money-validators";
import { HTTP_NO_CONTENT, HTTP_OK } from "../../utils/http-status";
import { balanceService } from "../../services/balance-service";
import { moneyFormatter } from "../../utils/money-formatter";

export default function BalanceField() {
    const [balance, setBalance] = useState("");
    const [balanceErrorMessage, setBalanceErrorMessage] = useState("");

    const updateBalanceValue = () => {
        balanceService.getValue()
            .then(res => {
                if (res.status !== HTTP_OK) {
                    return;
                }

                setBalance(res.body.balance);
            })
            .catch(err => console.log(err));
    }

    useEffect(() => {
        balanceService.getValue()
            .then(res => {
                if (res.status !== HTTP_OK) {
                    return;
                }

                setBalance(res.body.balance);
            })
            .catch(err => console.log(err));
    }, []);

    const updateBalance = model => {
        balanceService.update(model)
            .then(res => {
                if (res.status !== HTTP_NO_CONTENT) {
                    return;
                }

                updateBalanceValue();
            })
            .catch(err => console.log(err));
    }

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

        updateBalance(formModel);

        // const balance = moneyFormatter.mapStringToPenniesNumber(formModel.balance);
        // event.currentTarget.balance.value = moneyFormatter.mapPenniesNumberToString(balance);

    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 2 }}>
            <TextField
                margin="normal"
                fullWidth
                label="Account Balance"
                id="balance"
                name="balance"
                value={moneyFormatter.mapPenniesNumberToString(balance)}
                InputProps={{
                    endAdornment: <InputAdornment position="end">PLN</InputAdornment>,
                    readOnly: true
                }}
                error={!!balanceErrorMessage}
                helperText={balanceErrorMessage}
            />
            <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 1, mb: 2 }}
            >
                Update
            </Button>
        </Box>
    );
}
