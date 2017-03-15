# Labelingsystem Datahub

The Data Hub for connected Labeling System Labels.

## API Beispiele

### Projects

`Accept: application/json` `Accept: application/xml` `Accept: application/rdf+xml` `Accept: text/turtle` `Accept: text/n3` `Accept: application/ld+json` `Accept: application/rdf+json`

* GET http://localhost:8084/datahub/projects
* GET http://localhost:8084/datahub/projects/:id

`Content-Type: application/json`

* POST http://localhost:8084/datahub/projects

### Datasets

`Accept: application/json` `Accept: application/xml` `Accept: application/rdf+xml` `Accept: text/turtle` `Accept: text/n3` `Accept: application/ld+json` `Accept: application/rdf+json`

* GET http://localhost:8084/datahub/datasets
* GET http://localhost:8084/datahub/datasets/:id

`Content-Type: application/json`

* POST http://localhost:8084/datahub/datasets

### Search (get JSON)

* GET http://localhost:8084/datahub/search?project=p3zM1Dakdmde
* GET http://localhost:8084/datahub/search?publisher=http://rgzm.de
* GET http://localhost:8084/datahub/search?concept=http://143.93.114.135/item/label/804a0433-0b71-41a2-a003-d75dc53cad70
* GET http://localhost:8084/datahub/search?concept=http://143.93.114.135/item/label/ca1883f5-5e58-491e-8de5-ddb3a8d973a9
* GET http://localhost:8084/datahub/search?lat_min=0&lng_min=0&lat_max=55&lng_max=55
* GET http://localhost:8084/datahub/search?start=0&end=10

### labels used in the data hub (get JSON)

* GET http://localhost:8084/datahub/labels
