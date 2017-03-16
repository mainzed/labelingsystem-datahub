# API Documentation

## Projects

### GET projects

`Accept: application/json`

`Accept: application/xml`

`Accept: application/rdf+xml`

`Accept: text/turtle`

`Accept: text/n3`

`Accept: application/ld+json`

`Accept: application/rdf+json`

` GET http://localhost:8084/datahub/projects`

```json
[
	{ project }, { project }, { ... }
]
```

### GET project

`Accept: application/json`

`Accept: application/xml`

`Accept: application/rdf+xml`

`Accept: text/turtle`

`Accept: text/n3`

`Accept: application/ld+json`

`Accept: application/rdf+json`

` GET http://localhost:8084/datahub/projects/:id`

```json
{
	"id": "", --MENDATORY
	"title": "", --MENDATORY
	"publisher": "", --MENDATORY
	"description": "", --MENDATORY
	"license": "", --OPTIONAL
	"dump": "", --OPTIONAL
	"sparql": "" --MENDATORY
}
```

### POST project

`Content-Type: application/json`

` POST http://localhost:8084/datahub/projects`

```json
{
	"title": "", --MENDATORY
	"publisher": "",--MENDATORY
	"description": "", --MENDATORY
	"license": "", --MENDATORY
	"dump": "", --OPTIONAL
	"sparql": "", --OPTIONAL
	"token": "" --MENDATORY
}
```

## Datasets

### GET datasets

`Accept: application/json`

`Accept: application/xml`

`Accept: application/rdf+xml`

`Accept: text/turtle`

`Accept: text/n3`

`Accept: application/ld+json`

`Accept: application/rdf+json`

` GET http://localhost:8084/datahub/datasets`

### GET dataset

`Accept: application/json`

`Accept: application/xml`

`Accept: application/rdf+xml`

`Accept: text/turtle`

`Accept: text/n3`

`Accept: application/ld+json`

`Accept: application/rdf+json`

` GET http://localhost:8084/datahub/datasets/:id`

```json
{
  "id": "", --MENDATORY
  "project": "", --MENDATORY
	"relation": "", --OPTIONAL
	"dataset": "", --MENDATORY
	"label": "", --MENDATORY
	"title": "", --MENDATORY
	"description": "", --OPTIONAL
	"depiction": "", --OPTIONAL
	"coverage": "", --OPTIONAL
  "lat": "", --OPTIONAL
  "lng": "", --OPTIONAL
	"temporal": "", --OPTIONAL
  "begin": "", --OPTIONAL
  "end": "" --OPTIONAL
}
```

### POST dataset

`Content-Type: application/json`

` POST http://localhost:8084/datahub/datasets`

```json
{
	"project": "", --MENDATORY
	"relation": "", --OPTIONAL
	"dataset": "", --MENDATORY
	"label": "", --MENDATORY
	"title": "", --MENDATORY
	"description": "", --OPTIONAL
	"depiction": "", --OPTIONAL
	"coverage": "", --OPTIONAL
	"temporal": "", --OPTIONAL
  "begin": "", --OPTIONAL
  "end": "", --OPTIONAL
	"token": "" --MENDATORY
}
```

## Search

### metadata

#### get datasets by project ID

`param: project [String]`

` GET http://localhost:8084/datahub/search?project=4MDrEzxgG82v`

#### get datasets by publisher URL

`param: publisher [URI]`

` GET http://localhost:8084/datahub/search?publisher=http://rgzm.de`

### spatial and temporal attributes

#### get datasets by envelope (WGS84)

`param: lat_min [Double] lng_min [Double] lat_max [Double] lng_max [Double]`

` GET http://localhost:8084/datahub/search?lat_min=0&lng_min=0&lat_max=55&lng_max=55`

#### get datasets by publisher timespan (minus=BC)

`param: start [Integer] end [Integer]`

` GET http://localhost:8084/datahub/search?start=-100&end=250`

### description with concepts and resources

#### get datasets by concept URI

`param: concept [URI]`

` GET http://localhost:8084/datahub/search?concept=http://143.93.114.135/item/label/804a0433-0b71-41a2-a003-d75dc53cad70`

` GET http://localhost:8084/datahub/search?concept=http://143.93.114.135/item/label/ca1883f5-5e58-491e-8de5-ddb3a8d973a9`

#### get datasets by any resource as URI related to a concept

`param: resource [URI]`

` GET http://localhost:8084/datahub/search?resource=http://vocab.getty.edu/aat/300213080`

## labels used in the data hub

### get all labels used in the data hub

` GET http://localhost:8084/datahub/labels`

### get all labels used in the data hub filtered by language

`param: lang [String]`

` GET http://localhost:8084/datahub/labels&lang=de`
