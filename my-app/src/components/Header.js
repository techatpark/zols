import React from "react";
import { Link } from "react-router-dom";
import {
  Collapse,
  Navbar,
  NavbarToggler,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink
} from "reactstrap";

// The Header creates links that can be used to navigate
// between routes.
const Header = () => {
  return (
    <Nav>
      <NavItem>
        <NavLink href="/">Home</NavLink>
      </NavItem>
      <NavItem>
        <NavLink href="/roster">Roster</NavLink>
      </NavItem>
      <NavItem>
        <NavLink href="schedule">Schedule</NavLink>
      </NavItem>
    </Nav>
  );
};

export default Header;
