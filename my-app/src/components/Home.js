import React, { Component } from "react";
import Api from "../api";
import { Link } from "react-router-dom";
import { Card, CardBody, CardTitle, Row, Col } from "reactstrap";
export default class Home extends Component {
  state = {
    schemas: []
  };
  componentDidMount = async () => {
    const { data } = await Api.get(`/schema`);
    this.setState({ schemas: data });
  };
  render() {
    const { schemas } = this.state;
    return (
      <Row>
        {schemas.map((s, i) => (
          <Col key={i}>
            <Card>
              <CardBody>
                <CardTitle>
                  <Link to={`/data/${s.$id}`}>{s.title}</Link>
                </CardTitle>
              </CardBody>
            </Card>
          </Col>
        ))}
      </Row>
    );
  }
}
