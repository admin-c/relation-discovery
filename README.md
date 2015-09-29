# Reveal - NCSR - Language Preprocessing - Named Entity Recognition and Relation Extraction
					--- Updated by grv on 2015-09-29 17:04 ---

Module for Named Entity and Relation Extraction. Which process to be run can
be specified from command line arguments using -process <arg> , where <arg> is re
for Relation Extraction or ner for Named Entity Recognition.

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
