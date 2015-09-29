# Named Entity Recognition and Relation Extraction

Module for Named Entity and Relation Extraction. Module is based on Stanford corenlp and
mitie libraries for named entity recognition and on clausie and mitie libraries for
relation extraction. See [references section](#references) for more details.

## Running examples from jar
Which process to be run can be specified from command line arguments using -process <arg> ,
where <arg> is re for Relation Extraction or ner for Named Entity Recognition.

Since Named Entity Recognition is needed for Relation Extraction, in the case that re
is specified Named Entity Extraction will also run but will not output results.

#### usage: NER + RE extraction module
>  -h,--help            show help.
>  -i,--infile <arg>    The file containing JSON  representations of tweets
>                       or SAG posts - 1 per line default file looked for is default.json
>  -p,--process <arg>   Type of processing to do  ner for Named Entity
>                       Recognition re for Relation Extraction default is NER
>  -s,--isSAG           Whether to process as SAG posts default is off - if
>                       passed means process as SAG posts

Usage example - get help

>	java -jar RelationDiscovery-1.0-SNAPSHOT.jar -h

Usage example (with defaults):

>	java -jar RelationDiscovery-1.0-SNAPSHOT.jar

Usage example (specifics):

	(ner extraction from file default.json)
>	java -jar RelationDiscovery-1.0-SNAPSHOT.jar -i default.json -p ner

	(relation extraction from file default.json)
>	java -jar RelationDiscovery-1.0-SNAPSHOT.jar -i default.json -p ner

## Authors
* Gregory Katsios
* Andreas Grivas
* Anastasia Krithara

## References
* [clausie](https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/software/clausie)
* [stanford core nlp](http://nlp.stanford.edu/software/corenlp.shtml)
* [mitie](https://github.com/mit-nlp/MITIE)

## License
relation-extraction - NCSR Demokritos module
Copyright 2015 Gregory Katsios, Andreas Grivas, Anastasia Krithara

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
