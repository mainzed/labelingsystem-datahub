# API Documentation

## Table of Contents

1. [Projects](#projects)
    1. [GET projects](#GET-projects)
    2. [GET project](#GET-project)
    3. [POST project](#POST-project)
2. [Datasets](#Datasets)
    2. [GET datasets](#GET-datasets)
    3. [GET dataset](#GET-dataset)
    4. [POST dataset](#POST-dataset)
3. [Search](#Search)
    1. [metadata](#metadata)
        1. [get datasets by project ID](#get-datasets-by-project-ID)
        2. [get datasets by publisher URL](get-datasets-by-publisher-URL)
    2. [spatial and temporal attributes](#spatial-and-temporal-attributes)
        1. [get datasets by envelope (WGS84)](#get-datasets-by-envelope-wgs84)
        2. [get datasets by publisher timespan (minus=BC)](#get-datasets-by-publisher-timespan-minusbc)
    3. [description with concepts and resources](#description-with-concepts-and-resources)
        1. [get datasets by concept URI](#get-datasets-by-concept-URI)
        2. [get datasets by any resource as URI related to a concept](#get-datasets-by-any-resource-as-URI-related-to-a-concept)
4. [labels used in the data hub](#labels-used-in-the-data-hub)
    1. [get all labels used in the data hub](#get-all-labels-used-in-the-data-hub)
    2. [get all labels used in the data hub filtered by language](#get-all-labels-used-in-the-data-hub-filtered-by-language)


---------------------------------------


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
	"id": "wvKm5EJOwmGg",
	"project": "4MDrEzxgG82v",
	"title": "O12102",
	"dataset": "http://rgzm.de/navis/objects/001",
	"label": "http://143.93.114.135/item/label/412caba6-cd99-47fc-8465-9ee63eaee705",
	"description": "Ruderschiff mit Vorsegel, nach links",
	"relation": "http://www.w3.org/2000/01/rdf-schema#label",
	"depiction": "http://rgzm.de/Navis3/Middle/12102R00M.gif",
	"coverage": "http://sws.geonames.org/3169070",
	"lat": "41.89193",
	"lng": "12.51133",
	"temporal": "http://chronontology.dainst.org/period/rjqJUoIToI7L",
	"begin": "117",
	"end": "138",
}
```

* id: unique identifier `DEFAULT`
* project: id to the related project `MENDATORY`
* title: short dataset title `MENDATORY`
* dataset: link to your dataset URI `MENDATORY`
* label: link to the labelink.link concept `MENDATORY`
* description: short dataset description `OPTIONAL`
* relation: property that described the relation in your dataset `OPTIONAL`
* depiction: link to a depiction `OPTIONAL`
* coverage: link to GeoNames for spatial reference `OPTIONAL`
* lat: latitude by GeoNames `OPTIONAL`
* lng: longitude by GeoNames `OPTIONAL`
* temporal: link to ChronOntology for temporal reference `OPTIONAL`
* begin: year timespan start (manually or by ChronOntology) `OPTIONAL`
* end: year timespan end (manually or by ChronOntology) `OPTIONAL`

### POST dataset

`Content-Type: application/json`

` POST http://localhost:8084/datahub/datasets`

```json
{
	"project": "4MDrEzxgG82v",
	"title": "O12102",
	"dataset": "http://rgzm.de/navis/objects/001",
	"label": "http://143.93.114.135/item/label/412caba6-cd99-47fc-8465-9ee63eaee705",
	"description": "Ruderschiff mit Vorsegel, nach links",
	"relation": "http://www.w3.org/2000/01/rdf-schema#label",
	"depiction": "http://rgzm.de/Navis3/Middle/12102R00M.gif",
	"coverage": "http://sws.geonames.org/3169070",
	"temporal": "http://chronontology.dainst.org/period/rjqJUoIToI7L",
	"begin": "117",
	"end": "138",
	"token": "591c6a45-a26d-4564-81a8-a41da9d733f7"
}
```

* project: id to the related project `MENDATORY`
* title: short dataset title `MENDATORY`
* dataset: link to your dataset URI `MENDATORY`
* label: link to the labelink.link concept `MENDATORY`
* description: short dataset description `OPTIONAL`
* relation: property that described the relation in your dataset `OPTIONAL`
* depiction: link to a depiction `OPTIONAL`
* coverage: link to GeoNames for spatial reference (http://sws.geonames.org/:id) `OPTIONAL`
* temporal: link to ChronOntology for temporal reference (http://chronontology.dainst.org/period/:id) `OPTIONAL`
* begin: if no starting date for termporal reference available in ChronOntology use this attribute `OPTIONAL`
* end: if no ending date for termporal reference available in ChronOntology use this attribute `OPTIONAL`
* token: token to authentificate `MENDATORY`

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
