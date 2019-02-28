import React, { Component } from "react";
import { fetchSchema } from "../api";
import { Card, CardBody, CardTitle, CardSubtitle, Row, Col } from "reactstrap";
export default class Home extends Component {
  state = {
    schemas: []
  };
  componentDidMount = async () => {
    const { data } = await fetchSchema();
    this.setState({ schemas: data });
  };
  render() {
    const { schemas } = this.state;
    return (
      <Row>
        {schemas.map((s, i) => (
          <Col>
            <Card key={i}>
              <CardBody>
                <CardTitle>{s.title}</CardTitle>
              </CardBody>
              <CardBody>
                <CardSubtitle>
                  <pre>{JSON.stringify(s.properties, null, 2)}</pre>
                </CardSubtitle>
              </CardBody>
            </Card>
          </Col>
        ))}
      </Row>
    );
  }
}
