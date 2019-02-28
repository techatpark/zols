import React, { Component } from "react";
import { fetchAPlayer, updatePlayer } from "../api";
import { Link } from "react-router-dom";
import Form from "./PlayerForm";

// The Player looks up the player using the number parsed from
// the URL's pathname. If no player is fo und with the given
// number, then a "player not found" message is displayed.

export default class Player extends Component {
  state = {
    player: null
  };
  componentDidMount = async () => {
    const { data } = await fetchAPlayer(
      "number",
      this.props.match.params.number
    );
    this.setState({ player: data });
  };

  onChange = (value, key) => {
    this.setState(state => ({
      player: { ...state.player, [key]: value }
    }));
  };
  onSave = e => {
    e.preventDefault();
    updatePlayer(this.state.player)
      .then(res => {
        alert(res.status, "success");
      })
      .catch(e => {
        console.log(e);
      });
  };
  render() {
    const { player } = this.state;
    if (!player) {
      return <div>Sorry, but the player was not found</div>;
    }
    return (
      <div>
        <h1>(#{player.number})</h1>
        {player && (
          <Form
            player={player}
            onChange={this.onChange}
            savePlayer={this.onSave}
          />
        )}
        <Link to="/roster">Back</Link>
      </div>
    );
  }
}
