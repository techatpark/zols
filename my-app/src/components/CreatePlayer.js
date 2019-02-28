import React, { Component } from "react";
import Form from "./PlayerForm";
import { createAPlayer } from "../api";

export default class CreatePlayer extends Component {
  state = {
    player: {
      name: "",
      position: "",
      number: 0
    }
  };
  onChange = (value, key) => {
    this.setState(state => ({
      player: { ...state.player, [key]: value }
    }));
  };
  onSave = e => {
    e.preventDefault();
    const player = {
      ...this.state.player,
      number: parseInt(this.state.player.number)
    };
    createAPlayer(player)
      .then(res => {
        alert(res.status, "success");
      })
      .catch(e => {
        console.log(e);
      });
  };
  render() {
    return (
      <Form
        player={this.state.player}
        onChange={this.onChange}
        savePlayer={this.onSave}
      />
    );
  }
}
