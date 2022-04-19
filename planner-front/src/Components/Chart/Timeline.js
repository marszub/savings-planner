import React from "react";
import HorizontalTimeline from "react-horizontal-timeline";

const fakeGoals = [
    {
        name: "Zakup laptopa",
        price: 5000,
        date: "2022-05-21",
    },
    {
        name: "Remont mieszkania",
        price: 50000,
        date: "2024-01-21",
    },
    {
        name: "Zakup samochodu",
        price: 30000,
        date: "2023-01-26",
    },
    {
        name: "Wyjazd na Malediwy",
        price: 10000,
        date: "2022-12-03",
    },
    {
        name: "Kurs nurkowania",
        price: 3000,
        date: "2022-04-20",
    },
];

function compareDates(event1, event2) {
    if (event1.date < event2.date) return -1;
    else return 1;
 }
 

export default class Timeline extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      curIdx: 0,
      prevIdx: -1
    };
  }

  render() {
    fakeGoals.sort(compareDates)
    const { curIdx } = this.state;
    const curStatus = fakeGoals[curIdx].name;

    return (
      <div>
         <div className="goal-text">
          <a><b>Realizacja celu:</b> {curStatus}</a>
        </div>
        <div
          style={{
            height: "100px",
            margin: "0 auto",
            marginTop: "35px",
            fontSize: "15px"
          }}
        >
          <HorizontalTimeline
            styles={{
              background: "#ffffff",
              foreground: "#1A79AD",
              outline: "#dfdfdf"
            }}
            index={this.state.curIdx}
            indexClick={(index) => {
              const curIdx = this.state.curIdx;
              this.setState({ curIdx: index, prevIdx: curIdx });
            }}
            values={fakeGoals.map((x) => x.date)}
          />
        </div>
       
      </div>
    );
  }
}
