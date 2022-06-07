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
import EditIcon from '@mui/icons-material/Edit';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import { OneTimeEventCreateForm, CyclicEventCreateForm } from "../../models/event-create-form";
import { EventUpdateForm } from "../../models/event-update-form";
import { goalValidators } from '../../utils/goal-validators';
import { moneyValidators } from "../../utils/money-validators";
import { moneyFormatter } from "../../utils/money-formatter";
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import EventOutlinedIcon from '@mui/icons-material/EventOutlined';
import SavingsOutlinedIcon from '@mui/icons-material/SavingsOutlined';
import { Collapse, FormControlLabel, ListItemIcon, MenuItem, Switch } from "@mui/material";
import LoopIcon from '@mui/icons-material/Loop';
import "../../styles/Events.css";
import { HTTP_BAD_REQUEST, HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK } from "../../utils/http-status";
import { eventService } from "../../services/event-service";
import { INCOME_EVENT_TYPE, OUTGO_EVENT_TYPE } from "../../utils/event-types";
import { dateFormatter } from "../../utils/date-formatter";
import { DAY, MONTH, YEAR } from "../../utils/time-units";
import { eventValidators } from "../../utils/event-validators";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";


const theme = createTheme();

const EVENT_CREATED_ALERT = 'EVENT_CREATED';
const EVENT_DELETED_ALERT = 'EVENT_DELETED';
const EVENT_UPDATED_ALERT = 'EVENT_UPDATED';
const EVENT_404_ALERT = 'EVENT_404';

const eventTypes = [
    {
        value: INCOME_EVENT_TYPE,
        label: 'Income',
    },
    {
        value: OUTGO_EVENT_TYPE,
        label: 'Outgo',
    },
];

const timeUnits = [
    {
        value: DAY,
        label: 'Days',
    },
    {
        value: MONTH,
        label: 'Months',
    },
    {
        value: YEAR,
        label: 'Years',
    },
];

export default function EventList() {
    const [events, setEvents] = useState([]);
    const [eventCreationOpen, setEventCreationOpen] = useState(false);
    const [alertStatus, setAlertStatus] = useState("");
    const [refreshAlert, setRefreshAlert] = useState(false);

    const updateEventList = () => {
        eventService.getList()
            .then(res => {
                if (res.status !== HTTP_OK) {
                    return;
                }

                setEvents(res.body.events);
            })
            .catch(err => console.log(err));
    }

    useEffect(() => {
        eventService.getList()
            .then(res => {
                if (res.status !== HTTP_OK) {
                    return;
                }

                setEvents(res.body.events);
            })
            .catch(err => console.log(err));
    }, []);

    const createEvent = (cyclic, model) => {
        eventService.create(cyclic, model)
            .then(res => {
                if (res.status !== HTTP_CREATED) {
                    if (res.status === HTTP_BAD_REQUEST) console.log("Invalid request body");
                    return;
                }

                updateEventList();
                setAlertStatus(EVENT_CREATED_ALERT);
                setRefreshAlert(prev => !prev);
            })
            .catch(err => console.log(err));
    };

    const deleteEvent = id => {
        eventService.delete(id)
            .then(res => {
                switch (res.status) {
                    case HTTP_NO_CONTENT:
                        updateEventList();
                        setAlertStatus(EVENT_DELETED_ALERT);
                        setRefreshAlert(prev => !prev);
                        break;
                    case HTTP_NOT_FOUND:
                        setAlertStatus(EVENT_404_ALERT);
                        setRefreshAlert(prev => !prev);
                        break;
                    default:
                        console.log("Unexpected error");
                }
            })
            .catch(err => console.log(err));
    };

    const updateEvent = (model) => {
        eventService.update(model)
            .then(res => {
                if (res.status !== HTTP_NO_CONTENT) {
                    if (res.status === HTTP_BAD_REQUEST) console.log("Invalid request body");
                    return;
                }

                updateEventList();
                setAlertStatus(EVENT_UPDATED_ALERT);
                setRefreshAlert(prev => !prev);
            })
            .catch(err => console.log(err));
    }

    const eventsItems = events.map(event =>
        <Event
            key={event.id}
            event={event}
            isLast={event.id === events[events.length - 1].id}
            handleDelete={deleteEvent}
            handleUpdate={updateEvent}
        />
    );

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
                id="event-list"
            >
                <CssBaseline/>
                <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                    <EventAvailableOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Event list
                </Typography>

                <Container class="scrollable-list-events">
                    <List sx={{width: '100%', maxWidth: 1000, bgcolor: 'background.paper'}}>
                        { eventsItems }
                    </List>
                </Container>

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
    const [eventUpdateOpen, setEventUpdateOpen] = useState(false);
    const [nestedListOpen, setNestedListOpen] = useState(false);

    return (
        <>
            <ListItem>
                <Tooltip title={nestedListOpen ? 'Collapse' : 'Expand'}>
                    <IconButton
                        edge="start"
                        aria-label="expand"
                        onClick={() => setNestedListOpen(prev => !prev)}
                    >
                        {nestedListOpen ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                    </IconButton>
                </Tooltip>
                <ListItemText primary={props.event.title} sx={{ pl: 1 }} />
                <Tooltip title="Edit" sx={{ mr: 0.2 }}>
                    <IconButton
                        edge="end"
                        aria-label="edit"
                        onClick={() => setEventUpdateOpen(true)}
                    >
                        <EditIcon/>
                    </IconButton>
                </Tooltip>
                <Tooltip title="Delete">
                    <IconButton
                        edge="end"
                        aria-label="delete"
                        onClick={() => setEventRemovalOpen(true)}
                    >
                        <DeleteIcon/>
                    </IconButton>
                </Tooltip>
                <EventUpdateDialog
                    open={eventUpdateOpen}
                    onClose={() => setEventUpdateOpen(false)}
                    update={(model) => props.handleUpdate(model)}
                    event={props.event}
                />
                <EventRemovalConfirmationDialog
                    open={eventRemovalOpen}
                    onClose={() => setEventRemovalOpen(false)}
                    delete={() => props.handleDelete(props.event.id)}
                    event={props.event}
                />
            </ListItem>

            <Collapse in={nestedListOpen} timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                    { props.event.isCyclic &&
                        <ListItem sx={{ pl: 7 }}>
                            <ListItemIcon>
                                <LoopIcon />
                            </ListItemIcon>
                            <ListItemText primary="Cyclic" />
                        </ListItem>
                    }
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
                        <ListItemText primary={dateFormatter.formatDate(new Date(props.event.timestamp))} />
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
    const [cycleLengthErrorMessage, setCycleLengthErrorMessage] = useState("");
    const [eventType, setEventType] = useState(INCOME_EVENT_TYPE);
    const [beginEndErrorMessage, setBeginEndErrorMessage] = useState("");
    const [cyclic, setCyclic] = useState(false);
    const [cycleBase, setCycleBase] = useState(DAY);

    const handleClose = () => {
        props.onClose();
        setTitleErrorMessage('');
        setAmountErrorMessage('');
        setEventType(INCOME_EVENT_TYPE);
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);

        let formModel;
        if (cyclic) {
            formModel = new CyclicEventCreateForm(
                data.get('title'),
                data.get('event-type'),
                data.get('amount'),
                data.get('begins'),
                data.get('ends'),
                data.get('cycle-length'),
                data.get('cycle-base')
            );
        } else {
            formModel = new OneTimeEventCreateForm(
                data.get('title'),
                data.get('event-type'),
                data.get('amount'),
                data.get('date'),
            );
        }

        const titleError = eventValidators.validateTitle(formModel.title);
        const amountError = moneyValidators.validateAmount(formModel.amount);
        setTitleErrorMessage(titleError);
        setAmountErrorMessage(amountError);

        if (cyclic) {
            const beginsError = Object.values(event.currentTarget.begins)[1]['aria-invalid'];
            const endsError = Object.values(event.currentTarget.ends)[1]['aria-invalid'];
            const cycleLengthError = eventValidators.validateCycleLength(formModel.cycleLength);
            const beginEndError = eventValidators.validateBeginEnd(formModel.begins, formModel.ends);
            setCycleLengthErrorMessage(cycleLengthError);
            setBeginEndErrorMessage(beginEndError);

            if (titleError || amountError || beginsError || endsError || cycleLengthError || beginEndError) {
                return;
            }
        } else {
            const dateError = Object.values(event.currentTarget.date)[1]['aria-invalid'];

            if (titleError || amountError || dateError) {
                return;
            }
        }

        props.create(cyclic, formModel);
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
                            fullWidth
                            id="event-type"
                            name="event-type"
                            select
                            label="Select type"
                            value={eventType}
                            onChange={e => setEventType(e.target.value)}
                        >
                            {eventTypes.map((option) => (
                                <MenuItem key={option.value} value={option.value}>
                                    {option.label}
                                </MenuItem>
                            ))}
                        </TextField>
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
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={cyclic}
                                    onChange={e => setCyclic(e.target.checked)}
                                    inputProps={{ 'aria-label': 'controlled' }}
                                />
                            }
                            label="Cyclic"
                            labelPlacement="end"
                        />
                        <BasicDatePicker
                            name={cyclic ? 'begins' : 'date'}
                            label={cyclic ? 'Begins' : 'Date'}
                        />
                        { cyclic &&
                            <>
                            <BasicDatePicker
                                name="ends"
                                label="Ends"
                            />
                            <Box sx={{
                                display: 'flex',
                                gap: '8px'
                            }}>
                                <TextField
                                    sx={{
                                        flexBasis: '70%'
                                    }}
                                    type="number"
                                    margin="normal"
                                    required
                                    fullWidth
                                    id="cycle-length"
                                    name="cycle-length"
                                    label="Cycle length"
                                    error={!!cycleLengthErrorMessage}
                                    helperText={cycleLengthErrorMessage}
                                />
                                <TextField
                                    sx={{
                                        flexBasis: '30%'
                                    }}
                                    margin="normal"
                                    fullWidth
                                    id="cycle-base"
                                    name="cycle-base"
                                    select
                                    label="Cycle base"
                                    value={cycleBase}
                                    onChange={e => setCycleBase(e.target.value)}
                                >
                                    {timeUnits.map((option) => (
                                        <MenuItem key={option.value} value={option.value}>
                                            {option.label}
                                        </MenuItem>
                                    ))}
                                </TextField>
                            </Box>
                            <Box sx={{
                                color: 'red'
                            }}>
                                {beginEndErrorMessage}
                            </Box>
                            </>
                        }
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

function BasicDatePicker(props) {
    const [value, setValue] = useState(null);

    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
                label={props.label}
                value={value}
                onChange={(newValue) => {
                    setValue(newValue);
                }}
                renderInput={
                    (params) => <TextField
                        id="date"
                        name={props.name}
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

function EventUpdateDialog(props) {
    const [titleErrorMessage, setTitleErrorMessage] = useState("");
    const [amountErrorMessage, setAmountErrorMessage] = useState("");
    const [eventType, setEventType] = useState(props.event.amount < 0 ? OUTGO_EVENT_TYPE : INCOME_EVENT_TYPE);

    const handleChange = (event) => {
        setEventType(event.target.value);
    };

    const handleClose = () => {
        props.onClose();
        setTitleErrorMessage('');
        setAmountErrorMessage('');
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);
        const formModel = new EventUpdateForm(
            props.event.id,
            data.get('title'),
            data.get('event-type'),
            data.get('amount'),
            data.get('date')
        );

        const titleError = goalValidators.validateTitle(formModel.title);
        const amountError = moneyValidators.validateAmount(formModel.amount);
        const dateError = Object.values(event.currentTarget.date)[1]['aria-invalid'];

        setTitleErrorMessage(titleError);
        setAmountErrorMessage(amountError);

        if (titleError || amountError || dateError) {
            return;
        }

        props.update(formModel);
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
                <DialogTitle>Edit the event</DialogTitle>
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                >
                    <DialogContent>
                        <TextField
                            defaultValue={props.event.title}
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
                            fullWidth
                            id="event-type"
                            name="event-type"
                            select
                            label="Select type"
                            value={eventType}
                            onChange={handleChange}
                        >
                            {eventTypes.map((option) => (
                                <MenuItem key={option.value} value={option.value}>
                                    {option.label}
                                </MenuItem>
                            ))}
                        </TextField>
                        <TextField
                            defaultValue={moneyFormatter.mapPenniesNumberToString(Math.abs(props.event.amount))}
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
                        <BasicDateEditor
                            event={props.event}
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

function BasicDateEditor(props) {
    const [value, setValue] = useState(props.event.date);

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
            case EVENT_UPDATED_ALERT:
                setAlertSeverity('info');
                setAlertMessage('Event successfully updated!');
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
