# API Documentation

## Projects

### GET projects

`Accept: {application/json; application/xml; application/rdf+xml; text/turtle; text/n3; application/ld+json; application/rdf+json}`

` GET http://localhost:8084/datahub/projects`

```json
[
	{ project }, { project }, { ... }
]
```

### GET project

`Accept: {application/json; application/xml; application/rdf+xml; text/turtle; text/n3; application/ld+json; application/rdf+json}`

` GET http://localhost:8084/datahub/projects/:id`

**example**

```json
{
	"id": "4MDrEzxgG82v",
	"title": "Roman Coins of NAVIS",
	"description": "Roman coins of NAVIS database.",
	"publisher": "http://rgzm.de",
	"creator": "Römisch-Germanisches Zentralmuseum Mainz",
	"license": "http://creativecommons.org/licenses/by/4.0/",
	"datasets": "5",
	"dump": "http://rgzm.de/navis/dump.rdf",
	"sparql": "http://rgzm.de/navis/sparql"
}
```

* id: unique identifier `DEFAULT`
* title: short project title `DEFAULT`
* description: short project description `DEFAULT`
* publisher: publisher URL `DEFAULT`
* creator: creator as String `DEFAULT`
* license: link to a license `DEFAULT`
* datasets: count of related datasets `DEFAULT`
* dump: link to a RDF dump of the datasets `OPTIONAL`
* sparql: link to the SPARQL endpoint `OPTIONAL`

### POST project

`Content-Type: application/json`

` POST http://localhost:8084/datahub/projects`

**example**

```json
{
	"title": "Roman Coins of NAVIS",
	"description": "Roman coins of NAVIS database.",
	"publisher": "http://rgzm.de",
	"creator": "Römisch-Germanisches Zentralmuseum Mainz",
	"license": "http://creativecommons.org/licenses/by/4.0/",
	"dump": "http://rgzm.de/navis/dump.rdf",
	"sparql": "http://rgzm.de/navis/sparql",
	"token": "63ca991b-8def-4155-b0db-519e27cb07ea"
}
```

* title: short project title `MENDATORY`
* description: short project description `MENDATORY`
* publisher: publisher URL `MENDATORY`
* creator: creator as String `MENDATORY`
* license: link to a license `MENDATORY`
* dump: link to a RDF dump of the datasets `OPTIONAL`
* sparql: link to the SPARQL endpoint `OPTIONAL`
* token: token to authentificate `MENDATORY`

## Datasets

### GET datasets

`Accept: {application/json; application/xml; application/rdf+xml; text/turtle; text/n3; application/ld+json; application/rdf+json}`

` GET http://localhost:8084/datahub/datasets`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

### GET dataset

`Accept: {application/json; application/xml; application/rdf+xml; text/turtle; text/n3; application/ld+json; application/rdf+json}`

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

```json
[
	{ dataset }, { dataset }, { ... }
]
```

#### get datasets by publisher URL

`param: publisher [URI]`

` GET http://localhost:8084/datahub/search?publisher=http://rgzm.de`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

### spatial and temporal attributes

#### get datasets by envelope (WGS84)

`param: lat_min [Double] lng_min [Double] lat_max [Double] lng_max [Double]`

` GET http://localhost:8084/datahub/search?lat_min=0&lng_min=0&lat_max=55&lng_max=55`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

#### get datasets by publisher timespan (minus=BC)

`param: start [Integer] end [Integer]`

` GET http://localhost:8084/datahub/search?start=-100&end=250`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

### description with concepts and resources

#### get datasets by concept URI

`param: concept [URI]`

` GET http://localhost:8084/datahub/search?concept=http://143.93.114.135/item/label/ca1883f5-5e58-491e-8de5-ddb3a8d973a9`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

#### get datasets by any resource as URI related to a concept

`param: resource [URI]`

` GET http://localhost:8084/datahub/search?resource=http://vocab.getty.edu/aat/300213080`

```json
[
	{ dataset }, { dataset }, { ... }
]
```

## labels used in the data hub

### get all labels used in the data hub

` GET http://localhost:8084/datahub/labels`

**example**

```json
[
	{
		"datasets":1,
		"lang":"de",
		"uri":"http://143.93.114.135/item/label/ca1883f5-5e58-491e-8de5-ddb3a8d973a9",
		"value":"Paddel"
	},
	{ label },
	{ ... }
]
```

### get all labels used in the data hub filtered by language

`param: lang [String]`

` GET http://localhost:8084/datahub/labels&lang=de`

**example**

```json
[
	{
		"datasets":1,
		"lang":"de",
		"uri":"http://143.93.114.135/item/label/ca1883f5-5e58-491e-8de5-ddb3a8d973a9",
		"value":"Paddel"
	},
	{ label },
	{ ... }
]
```
