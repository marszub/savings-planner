import {InputAdornment, TextField, ThemeProvider} from "@mui/material";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { BalanceUpdateForm } from "../../models/balance-update-form";
import { moneyValidators } from "../../utils/money-validators";
import { HTTP_NO_CONTENT, HTTP_OK } from "../../utils/http-status";
import { balanceService } from "../../services/balance-service";
import { moneyFormatter } from "../../utils/money-formatter";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import * as React from "react";
import Container from "@mui/material/Container";
import { createTheme } from "@mui/material/styles";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";


const theme = createTheme();

const BALANCE_EDITED_ALERT = 'BALANCE_EDITED';

export default function BalanceField() {
    const [balance, setBalance] = useState("");
    const [balanceEditOpen, setBalanceEditOpen] = useState(false);
    const [alertStatus, setAlertStatus] = useState("");
    const [refreshAlert, setRefreshAlert] = useState(false);

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

    const editBalance = model => {
        balanceService.update(model)
            .then(res => {
                console.log(res.status);
                if (res.status !== HTTP_NO_CONTENT) {
                    return;
                }

                updateBalanceValue();
                setAlertStatus(BALANCE_EDITED_ALERT);
                setRefreshAlert(prev => !prev);
            })
            .catch(err => console.log(err));
    }

    return (
        <ThemeProvider theme={theme}>
            <Container
                component="main"
                maxWidth="xs"
                sx={{
                    marginTop: 3,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
                id="balance-field"
            >
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
                />
                <Button
                    fullWidth
                    variant="contained"
                    sx={{ mt: 1, mb: 2 }}
                    onClick={() => setBalanceEditOpen(true)}
                >
                    Update
                </Button>

                <BalanceUpdateDialog
                    open={balanceEditOpen}
                    onClose={() => setBalanceEditOpen(false)}
                    edit={model => editBalance(model)}
                    balance={balance}
                />

                <EventActionSnackbar alert={alertStatus} refresh={refreshAlert}/>
            </Container>
        </ThemeProvider>
    );
}

function BalanceUpdateDialog(props) {
    const [amountErrorMessage, setAmountErrorMessage] = useState("");

    const handleClose = () => {
        props.onClose();
        setAmountErrorMessage('');
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new BalanceUpdateForm(
            data.get('amount')
        );

        const amountError = moneyValidators.validateAmount(formModel.balance);

        setAmountErrorMessage(amountError);

        if (amountError) {
            return;
        }

        props.edit(formModel);
        handleClose();
    };

    return (
        <Dialog
            maxWidth="xs"
            open={props.open}
            onClose={handleClose}
        >
            <Box
                sx={{
                    margin: 1
                }}
            >
                <DialogTitle>Edit account balance</DialogTitle>
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                >
                    <DialogContent>
                        <TextField
                            defaultValue={moneyFormatter.mapPenniesNumberToString(props.balance)}
                            margin="normal"
                            required
                            fullWidth
                            id="amount"
                            label="Amount"
                            name="amount"
                            error={!!amountErrorMessage}
                            helperText={amountErrorMessage}
                            InputProps={{
                                endAdornment: <InputAdornment position="end">PLN</InputAdornment>,
                            }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button
                            type="button"
                            onClick={handleClose}
                        >
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            variant="contained"
                        >
                            Update
                        </Button>
                    </DialogActions>
                </Box>
            </Box>
        </Dialog>
    );
}

function EventActionSnackbar(props) {
    const [alertOpen, setAlertOpen] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');

    const updateAlert = () => {
        switch (props.alert) {
            case '':
                setAlertOpen(false);
                break;
            case BALANCE_EDITED_ALERT:
                setAlertMessage('Balance successfully updated!');
                setAlertOpen(true);
                break;
            default:
                console.log('Unknown alert status ' + props.alert);
                break;
        }
    };

    const handleAlertClose = () => {
        setAlertOpen(false);
    }

    useEffect(() => updateAlert(), [props.refresh]);

    return (
        <Snackbar
            open={alertOpen}
            autoHideDuration={5000}
            onClose={handleAlertClose}
        >
            <Alert
                severity="info"
                variant="filled"
                onClose={handleAlertClose}
            >
                {alertMessage}
            </Alert>
        </Snackbar>
    );
}
