# JSON

## POST project

`POST /datahub/projects`

`Content-Type: application/json`

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

## GET project

`GET /datahub/projects/:id`

`Accept: application/json`

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

## POST dataset

`POST /datahub/datasets`

`Content-Type: application/json`

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

## GET dataset

`GET /datahub/datasets/:id`

`Accept: application/json`

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
