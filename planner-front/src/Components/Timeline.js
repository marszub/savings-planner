import React from "react";
import HorizontalTimeline from "react-horizontal-timeline";
import { Goals } from "../services/mockData";

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
            values={Goals.map((x) => x.date)}
          />
        </div>
       
      </div>
    );
  }
}
