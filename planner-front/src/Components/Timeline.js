import React from "react";
import HorizontalTimeline from "react-horizontal-timeline";
import { UserData, Goals } from "../services/mockData";

function compare(a, b) {
    if (a.date < b.date)
       return -1
    else
       return 1
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
    Goals.sort(compare)
    const { curIdx } = this.state;
    const curStatus = Goals[curIdx].name;

    return (
      <div>
        <div
          style={{
            height: "100px",
            margin: "0 auto",
            marginTop: "20px",
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
            values={Goals.map((x) => x.date)}
          />
        </div>
        <div className="goal-text">
          <a>{curStatus}</a>
        </div>
      </div>
    );
  }
}
