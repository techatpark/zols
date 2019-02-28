import React, { Component } from "react";
import { fetchPlayers } from "../api";
import { Link } from "react-router-dom";

// The FullRoster iterates over all of the players and creates
// a link to their profile page.

export default class FullRoster extends Component {
  state = {
    players: []
  };
  componentDidMount = async () => {
    const { data } = await fetchPlayers();
    this.setState({ players: data.content });
  };
  render() {
    const { players } = this.state;
    return (
      <div>
        <ul>
          {players &&
            players.map(p => (
              <li key={p.number}>
                <Link to={`/roster/${p.number}`}>{p.name}</Link>
              </li>
            ))}
        </ul>
      </div>
    );
  }
}
