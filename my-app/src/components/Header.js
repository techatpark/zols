import React from "react";
import { Nav, NavItem, NavLink } from "reactstrap";

// The Header creates links that can be used to navigate
// between routes.
const Header = () => {
  return (
    <Nav>
      <NavItem>
        <NavLink href="/">Home</NavLink>
      </NavItem>
    </Nav>
  );
};

export default Header;
