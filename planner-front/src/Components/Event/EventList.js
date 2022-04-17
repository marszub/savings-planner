import * as React from 'react';
import { useEffect, useState } from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import Alert from '@mui/material/Alert';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Snackbar from '@mui/material/Snackbar';
import Tooltip from '@mui/material/Tooltip';
import EventAvailableOutlinedIcon from '@mui/icons-material/EventAvailableOutlined';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import DeleteIcon from '@mui/icons-material/Delete';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import { EventCreateForm } from "../../models/event-create-form";
import { goalValidators } from '../../utils/goal-validators';
import { moneyFormatter } from "../../utils/money-formatter";
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import EventOutlinedIcon from '@mui/icons-material/EventOutlined';
import SavingsOutlinedIcon from '@mui/icons-material/SavingsOutlined';
import { Collapse, ListItemButton, ListItemIcon } from "@mui/material";
import { ExpandLess, ExpandMore } from "@mui/icons-material";

const theme = createTheme();

const EVENT_CREATED_ALERT = 'EVENT_CREATED';
const EVENT_DELETED_ALERT = 'EVENT_DELETED';

const fakeEvents = [
    {
        id: 1,
        title: 'Event Title 1',
        amount: 100000,
        date: '04/16/2022'
    },
    {
        id: 2,
        title: 'Event Title 2',
        amount: 200000,
        date: '03/19/2020'
    },
    {
        id: 3,
        title: 'Event Title 3',
        amount: 300000,
        date: '03/13/2019'
    },
    {
        id: 4,
        title: 'Event Title 4',
        amount: 400000,
        date: '12/18/2021'
    },
    {
        id: 5,
        title: 'Event Title 5',
        amount: 500000,
        date: '05/23/2004'
    }
];

export default function EventList() {
    const [events, setEvents] = useState(fakeEvents);
    const [eventCreationOpen, setEventCreationOpen] = useState(false);
    const [alertStatus, setAlertStatus] = useState("");
    const [refreshAlert, setRefreshAlert] = useState(false);

    const findNewId = () => Math.max(...events.map(event => event.id)) + 1;

    const createEvent = (model) => {
        setEvents(prev => [
            ...prev,
            {
                id: findNewId(),
                title: model.title,
                amount: moneyFormatter.mapStringToPenniesNumber(model.amount),
                date: model.date
            }
        ]);

        setAlertStatus(EVENT_CREATED_ALERT);
        setRefreshAlert(prev => !prev);
    };

    const deleteEvent = (eventId) => {
        setEvents(prev => [...prev.filter(event => event.id !== eventId)]);
        setAlertStatus(EVENT_DELETED_ALERT);
        setRefreshAlert(prev => !prev);
    };

    const eventsItems = events.map(event =>
        <Event
            key={event.id}
            event={event}
            isLast={event.id === events[events.length - 1].id}
            handleDelete={deleteEvent}
        />
    );

    return (
        <ThemeProvider theme={theme}>
            <Container
                component="main"
                maxWidth="xs"
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <CssBaseline/>
                <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                    <EventAvailableOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Event list
                </Typography>

                <List sx={{width: '100%', maxWidth: 360, bgcolor: 'background.paper'}}>
                    { eventsItems }
                </List>

                <Button
                    fullWidth
                    variant="contained"
                    sx={{mt: 3, mb: 2}}
                    onClick={() => setEventCreationOpen(true)}
                >
                    Add new event
                </Button>

                <EventCreationDialog
                    open={eventCreationOpen}
                    onClose={() => setEventCreationOpen(false)}
                    create={createEvent}
                />

                <EventActionSnackbar alert={alertStatus} refresh={refreshAlert}/>
            </Container>
        </ThemeProvider>
    );
}

function Event(props) {
    const [eventRemovalOpen, setEventRemovalOpen] = useState(false);
    const [nestedListOpen, setNestedListOpen] = useState(false);

    const handleClick = () => {
        setNestedListOpen(!nestedListOpen);
    }

    return (
        <>
            <ListItemButton onClick={handleClick}>
                { nestedListOpen ? <ExpandLess /> : <ExpandMore /> }
                <ListItemText primary={props.event.title} sx={{ pl: 1 }} />
                <Tooltip title="Delete">
                    <IconButton
                        edge="end"
                        aria-label="delete"
                        onClick={() => setEventRemovalOpen(true)}
                    >
                        <DeleteIcon/>
                    </IconButton>
                </Tooltip>
                <EventRemovalConfirmationDialog
                    open={eventRemovalOpen}
                    onClose={() => setEventRemovalOpen(false)}
                    delete={() => props.handleDelete(props.event.id)}
                    event={props.event}
                />
            </ListItemButton>

            <Collapse in={nestedListOpen} timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                    <ListItem sx={{ pl: 7 }}>
                        <ListItemIcon>
                            <SavingsOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText primary={moneyFormatter.mapPenniesNumberToString(props.event.amount) + ' PLN'} />
                    </ListItem>
                    <ListItem sx={{ pl: 7 }}>
                        <ListItemIcon>
                            <EventOutlinedIcon />
                        </ListItemIcon>
                        <ListItemText primary={props.event.date} />
                    </ListItem>
                </List>
            </Collapse>

            {!props.isLast &&
                <Divider/>
            }
        </>
    );
}

function EventCreationDialog(props) {
    const [titleErrorMessage, setTitleErrorMessage] = useState("");
    const [amountErrorMessage, setAmountErrorMessage] = useState("");

    const handleClose = () => {
        props.onClose();
        setTitleErrorMessage('');
        setAmountErrorMessage('');
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new EventCreateForm(
            data.get('title'),
            data.get('amount'),
            data.get('date')
        );

        const titleError = goalValidators.validateTitle(formModel.title);
        const amountError = goalValidators.validateAmount(formModel.amount);
        const dateError = Object.values(event.currentTarget.date)[1]['aria-invalid'];

        setTitleErrorMessage(titleError);
        setAmountErrorMessage(amountError);

        if (titleError || amountError || dateError) {
            return;
        }

        props.create(formModel);
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
                <DialogTitle>Create new event</DialogTitle>
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                >
                    <DialogContent>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="title"
                            label="Title"
                            name="title"
                            autoFocus
                            error={!!titleErrorMessage}
                            helperText={titleErrorMessage}
                        />
                        <TextField
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
                        <BasicDatePicker />
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
                            Create
                        </Button>
                    </DialogActions>
                </Box>
            </Box>
        </Dialog>
    );
}

function BasicDatePicker() {
    const [value, setValue] = useState(null);

    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
                label="Date"
                value={value}
                onChange={(newValue) => {
                    setValue(newValue);
                }}
                renderInput={
                    (params) => <TextField
                        id="date"
                        name="date"
                        {...params}
                        required
                        fullWidth
                        sx={{ mt: 2, mb: 1 }}
                    />
                }
            />
        </LocalizationProvider>
    );
}

function EventRemovalConfirmationDialog(props) {
    const handleDelete = () => {
        props.delete();
        props.onClose();
    }

    return (
        <Dialog
            maxWidth="xs"
            open={props.open}
            onClose={props.onClose}
        >
            <Box
                sx={{
                    margin: 1
                }}
            >
                <DialogTitle>Confirmation</DialogTitle>
                <DialogContent>
                    This action cannot be undone. Are you sure you want to delete the event <strong>{props.event.title}</strong>?
                </DialogContent>
                <DialogActions>
                    <Button
                        type="button"
                        onClick={props.onClose}
                    >
                        Cancel
                    </Button>
                    <Button
                        type="button"
                        variant="contained"
                        color="warning"
                        onClick={handleDelete}
                    >
                        Delete
                    </Button>
                </DialogActions>
            </Box>
        </Dialog>
    )
}

function EventActionSnackbar(props) {
    const [alertOpen, setAlertOpen] = useState(false);
    const [alertSeverity, setAlertSeverity] = useState('info');
    const [alertMessage, setAlertMessage] = useState('');

    const updateAlert = () => {
        switch (props.alert) {
            case '':
                setAlertOpen(false);
                break;
            case EVENT_CREATED_ALERT:
                setAlertSeverity('success');
                setAlertMessage('New event successfully created!');
                setAlertOpen(true);
                break;
            case EVENT_DELETED_ALERT:
                setAlertSeverity('info');
                setAlertMessage('Event successfully deleted!');
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
                severity={alertSeverity}
                variant="filled"
                onClose={handleAlertClose}
            >
                {alertMessage}
            </Alert>
        </Snackbar>
    );
}
