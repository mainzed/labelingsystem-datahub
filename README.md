# Labeling System DataHub

The Labeling System offers experts the possibility to create concepts with context-bound validity, to concretize, to group in containers (vocabularies) and to share them with the research community. The LS provides user-friendly web tools that allow semantic linking of terms into the Linked Open Data Cloud. Once vocabularies are published, the LS serves as a distributed repository of concepts (concept-gazetteer), which provides citable addresses on the Web (URI). Each generated concept is explicit assigned to its creator. This assured authorship yield in a clear responsibility for data maintenance.

The Labeling System consists of three components: the [server](https://github.com/mainzed/labelingsystem-server), the [client wep-app](https://github.com/mainzed/labelingsystem-client) and the [datahub](https://github.com/mainzed/labelingsystem-datahub). The [datamodel](https://github.com/mainzed/labelingsystem-ontology) used in the backend is represented in an ontology using linked data vocabularies. This repository represents the server component of the Labeling System.

## DataHub Components

The Labeling System DataHub consists of two components: The `API` and the `Explorer`.

## Why a DataHub for the Labeling System?

Labeling System concepts can be used in your own database or Linked Data application. We as owner of the Labeling System may not know, that you have connected your data to a Labeling System concept. The Labeling System Data Hub offers the possibility to send easyly datasets of a project, including metadata, a thumbnail, spatial and temporal references via POST request to the Data Hub triplestore. Enable people of the scientific community to explore your data using Labeling System concepts! The Data Hub serves as exploring portal for concept connections created in the "Concept-Gezetteer" Labeling System.

The Data Hub is following the example of [Pelagios](http://commons.pelagios.org) / [Pleiades](http://pleiades.stoa.org) / [Pelagios Cookbook](https://github.com/pelagios/pelagios-cookbook/wiki/Joining-Pelagios) and [Nomisma](http://nomisma.org).

## How to contribute?

First, use Labeling System concepts in your database or application for describing items. Your item / object / dataset must have an URI and have to be open accessible.

Second, send an email to `labeling@mainzed.org` and get a authentification key.

Third, create a new project via POST request.

Last but not least, create datasets via POST request.

Enjoy!

![datamodel](../../raw/master/img/contribute.png)

## The Labeling System Data Hub Model

![datamodel](../../raw/master/img/datamodel-simple.png)

## Demo

* [API Demo Server](http://ls-dev.i3mainz.hs-mainz.de/explorer)

## Config

* modify `config[XXX].properties` and mkdir for dumping `/opt/tomcat/webapps/dump-dh/` with `chmod 777 -R <directory>`

## License

### non code

This work is licensed under a [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/).

CC BY [Florian Thiery M.Sc.](http://orcid.org/0000-0002-3246-3531), i3mainz, RGZM

### Sourcecode

MIT License

Copyright (c) 2017 Florian Thiery M.Sc., i3mainz, RGZM

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
