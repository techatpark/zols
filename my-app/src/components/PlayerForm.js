import React from "react";
const position = ["G", "F", "D", "M"];
export default props => {
  return (
    <div>
      <form onSubmit={props.savePlayer}>
        <label>
          Number:
          <input
            value={props.player.number}
            key="number"
            type="number"
            onChange={e => props.onChange(e.target.value, "number")}
          />
        </label>
        <label>
          Name:
          <input
            value={props.player.name}
            placeholder="Enter name of player"
            key="name"
            type="text"
            onChange={e => props.onChange(e.target.value, "name")}
          />
        </label>
        <label>
          Position:
          <select
            value={props.player.position}
            onChange={e => props.onChange(e.target.value, "position")}
          >
            {position.map(p => (
              <option key={p}>{p}</option>
            ))}
          </select>
        </label>
        <input type="submit" value="Save" />
      </form>
    </div>
  );
};
