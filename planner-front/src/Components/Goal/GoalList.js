import * as React from 'react';
import {useEffect, useState} from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
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
import SportsScoreOutlinedIcon from '@mui/icons-material/SportsScoreOutlined';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import DeleteIcon from '@mui/icons-material/Delete';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import {GoalCreateForm} from '../../models/goal-create-form';
import {goalValidators} from '../../utils/goal-validators';

const theme = createTheme();

const GOAL_CREATED_ALERT = 'GOAL_CREATED';
const GOAL_DELETED_ALERT = 'GOAL_DELETED';

const fakeGoals = [
  {
    id: 1,
    title: 'Goal Title 1',
    amount: 1000
  },
  {
    id: 2,
    title: 'Goal Title 2',
    amount: 2000
  },
  {
    id: 3,
    title: 'Goal Title 3',
    amount: 3000
  },
  {
    id: 4,
    title: 'Goal Title 4',
    amount: 4000
  },
  {
    id: 5,
    title: 'Goal Title 5',
    amount: 5000
  }
];

export default function GoalList() {
  const [goals, setGoals] = useState(fakeGoals);
  const [goalCreationOpen, setGoalCreationOpen] = useState(false);
  const [alertStatus, setAlertStatus] = useState("");
  const [refreshAlert, setRefreshAlert] = useState(false);

  const handleGoalCreationOpen = () => {
    setGoalCreationOpen(true);
  };

  const handleGoalCreationClose = () => {
    setGoalCreationOpen(false);
  };

  const findNewId = () => {
    return Math.max(...goals.map(goal => goal.id)) + 1;
  };

  const createGoal = (model) => {
    setGoals(prev => [
        ...prev,
        {
          id: findNewId(),
          title: model.title,
          amount: model.amount
        }
    ]);

    setGoalCreationOpen(false);
    setAlertStatus(GOAL_CREATED_ALERT);
    setRefreshAlert(prev => !prev);
  };

  const deleteGoal = (goalId) => {
    setGoals(prev => [...prev.filter(goal => goal.id !== goalId)]);
    setAlertStatus(GOAL_DELETED_ALERT);
    setRefreshAlert(prev => !prev);
  };

  const goalsItems = goals.map(goal =>
      <Goal
          key={ goal.id }
          goal={goal}
          isLast={goal.id === goals[goals.length - 1].id}
          handleDelete={deleteGoal}
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
          <CssBaseline />
          <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <SportsScoreOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Goal list
          </Typography>

          <List sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }} >
            { goalsItems }
          </List>

          <Button
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              onClick={handleGoalCreationOpen}
          >
            Add new goal
          </Button>

          <GoalCreationDialog
              open={goalCreationOpen}
              onClose={handleGoalCreationClose}
              create={createGoal}
          />

          <GoalActionSnackbar alert={alertStatus} refresh={refreshAlert}/>
        </Container>
      </ThemeProvider>
  );
}

function Goal(props) {
  return (
      <>
          <ListItem >
            <ListItemText
                primary={ props.goal.title }
                secondary={ props.goal.amount + ' PLN' }
            />
            <Tooltip title="Delete">
              <IconButton
                  edge="end"
                  aria-label="delete"
                  onClick={() => props.handleDelete(props.goal.id)}
              >
                <DeleteIcon />
              </IconButton>
            </Tooltip>
          </ListItem>

          { !props.isLast &&
          <Divider />
          }
      </>
  );
}

function GoalCreationDialog(props) {
  const [titleErrorMessage, setTitleErrorMessage] = useState("");
  const [amountErrorMessage, setAmountErrorMessage] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();

    const data = new FormData(event.currentTarget);
    const formModel = new GoalCreateForm(
        data.get('title'),
        data.get('amount')
    );

    const titleError = goalValidators.validateTitle(formModel.title);
    const amountError = goalValidators.validateAmount(formModel.amount);

    setTitleErrorMessage(titleError);
    setAmountErrorMessage(amountError);

    if (titleError || amountError) {
      return;
    }

    props.create(formModel);
  }

  return (
      <Dialog
          maxWidth="xs"
          open={props.open}
          onClose={props.onClose}
      >
        <Box
            sx={{
              margin: 2
            }}
        >
        <DialogTitle>Create new goal</DialogTitle>
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
                type="number"
                margin="normal"
                required
                fullWidth
                id="amount"
                label="Amount"
                name="amount"
                autoFocus
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
                onClick={props.onClose}
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

function GoalActionSnackbar(props) {
  const [alertOpen, setAlertOpen] = useState(false);
  const [alertSeverity, setAlertSeverity] = useState('info');
  const [alertMessage, setAlertMessage] = useState('');

  const updateAlert = () => {
    switch (props.alert) {
      case '':
        setAlertOpen(false);
        break;
      case GOAL_CREATED_ALERT:
        setAlertSeverity('success');
        setAlertMessage('New goal successfully created!');
        setAlertOpen(true);
        break;
      case GOAL_DELETED_ALERT:
        setAlertSeverity('info');
        setAlertMessage('Goal successfully deleted!');
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
