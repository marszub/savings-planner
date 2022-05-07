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
import {moneyValidators} from "../../utils/money-validators";
import {moneyFormatter} from "../../utils/money-formatter";
import {goalService} from "../../services/goal-service";
import {HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK} from "../../utils/http-status";

const theme = createTheme();

const GOAL_CREATED_ALERT = 'GOAL_CREATED';
const GOAL_DELETED_ALERT = 'GOAL_DELETED';
const GOAL_404_ALERT = 'GOAL_404';

export default function GoalList() {
  const [goals, setGoals] = useState([]);
  const [goalCreationOpen, setGoalCreationOpen] = useState(false);
  const [alertStatus, setAlertStatus] = useState("");
  const [refreshAlert, setRefreshAlert] = useState(false);

  const updateGoalList = () => {
      goalService.getList()
          .then(res => {
              if (res.status !== HTTP_OK) {
                  return;
              }

              setGoals(res.body.goals);
          })
          .catch(err => console.log(err));
  }

  useEffect(() => {
    goalService.getList()
        .then(res => {
          if (res.status !== HTTP_OK) {
            return;
          }

          setGoals(res.body.goals);
        })
        .catch(err => console.log(err));
    }, []);

  const createGoal = model => {
    goalService.create(model)
        .then(res => {
          if (res.status !== HTTP_CREATED) {
            return;
          }

            updateGoalList();
            setAlertStatus(GOAL_CREATED_ALERT);
            setRefreshAlert(prev => !prev);
        })
        .catch(err => console.log(err));
  };

  const deleteGoal = id => {
    goalService.delete(id)
        .then(res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              updateGoalList();
                setAlertStatus(GOAL_DELETED_ALERT);
                setRefreshAlert(prev => !prev);
              break;
            case HTTP_NOT_FOUND:
              setAlertStatus(GOAL_404_ALERT);
              setRefreshAlert(prev => !prev);
              break;
            default:
              console.log("Unexpected error");
          }
        })
        .catch(err => console.log(err));
  };

  const goalsItems = goals.map(goal =>
      <Goal
          key={goal.id}
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
          <CssBaseline/>
          <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
            <SportsScoreOutlinedIcon/>
          </Avatar>
          <Typography component="h1" variant="h5">
            Goal list
          </Typography>

          <List sx={{width: '100%', maxWidth: 360, bgcolor: 'background.paper'}}>
            {goalsItems}
          </List>

          <Button
              fullWidth
              variant="contained"
              sx={{mt: 3, mb: 2}}
              onClick={() => setGoalCreationOpen(true)}
          >
            Add new goal
          </Button>

          <GoalCreationDialog
              open={goalCreationOpen}
              onClose={() => setGoalCreationOpen(false)}
              create={createGoal}
          />

          <GoalActionSnackbar alert={alertStatus} refresh={refreshAlert}/>
        </Container>
      </ThemeProvider>
  );
}

function Goal(props) {
  const [goalRemovalOpen, setGoalRemovalOpen] = useState(false);

  return (
      <>
        <ListItem>
          <ListItemText
              primary={props.goal.title}
              secondary={moneyFormatter.mapPenniesNumberToString(props.goal.amount) + ' PLN'}
          />
          <Tooltip title="Delete">
            <IconButton
                edge="end"
                aria-label="delete"
                onClick={() => setGoalRemovalOpen(true)}
            >
              <DeleteIcon/>
            </IconButton>
          </Tooltip>
          <GoalRemovalConfirmationDialog
              open={goalRemovalOpen}
              onClose={() => setGoalRemovalOpen(false)}
              delete={() => props.handleDelete(props.goal.id)}
              goal={props.goal}
          />
        </ListItem>

        {!props.isLast &&
            <Divider/>
        }
      </>
  );
}

function GoalCreationDialog(props) {
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
    const formModel = new GoalCreateForm(
        data.get('title'),
        data.get('amount')
    );

    const titleError = goalValidators.validateTitle(formModel.title);
    const amountError = moneyValidators.validateAmount(formModel.amount);

    setTitleErrorMessage(titleError);
    setAmountErrorMessage(amountError);

    if (titleError || amountError) {
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
                Create
              </Button>
            </DialogActions>
          </Box>
        </Box>
      </Dialog>
  );
}

function GoalRemovalConfirmationDialog(props) {
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
            This action cannot be undone. Are you sure you want to delete the goal <strong>{props.goal.title}</strong>?
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
      case GOAL_404_ALERT:
        setAlertSeverity("error");
        setAlertMessage("This goal does not exist!");
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

  useEffect(() => {
      updateAlert();
  }, [props.refresh]);

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
