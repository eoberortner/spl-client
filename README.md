# BOOST REST API Client

The Build-OptimizatiOn Software Tools (BOOST) provide a REST API to access the following 
functionalities programmatically:
- Reverse-translation of protein sequences into DNA sequences
- Codon juggling of protein coding regions within DNA sequences
- Verification of DNA sequences against DNA synthesis constraints
- Modification of protein coding regions that violate DNA synthesis constraints
- Partitioning of DNA sequences into synthesizable building blocks

The data exchanged between a client and the BOOST REST API is represented in JavaScript Object Notation (JSON). 
After a successful login, every request from a client to the BOOST REST API requires a 
<a href="https://jwt.io/" target="_blank">JSON Web Token (JWT)</a> in the message header.

We have developed a simple Java client that abstracts JSON data representation and the JWT-based authentication mechanism 
by providing a method for each functionality of the BOOST REST API.

## Utilization of the BOOST Client

### Instantiating the BOOST Client
 
As first step, you have to instantiate the BOOSTClient class providing your BOOST username and password.

```
BOOSTClient client = new BOOSTClient("username", "password");
```

### Supported methods of the BOOST Client

* reverseTranslate

```
client.reverseTranslate(
	"./protein_sequences.fasta",	// a FASTA file containing the input sequences 
	Strategy.MostlyUsed, 			// the codon selection strategy
	"./data/Ecoli.cudb", 			// the codon usage table of the target host
	FileFormat.GENBANK);			// the desired output format
```

* reverseTranslate

```
client.codonJuggle(
	"./dna_sequence.fasta",			// a FASTA file containing the input sequences 
	true,							// are all sequences 5'-3' protein coding sequencese xclusively  
	Strategy.MostlyUsed,			// the codon replacement strategy
	"./data/Ecoli.cudb", 			// the codon usage table of the target host
	FileFormat.GENBANK);			// the desired output format
```
## Examples 

An example of invoking every supported method is provided in the [DemoClient](https://github.com/eoberortner/BOOST-REST-Client/blob/master/src/test/java/gov/doe/jgi/boost/client/DemoClient.java) 

## Questions, Feature Requests, or Bug-Report?

Please contact ```eoberortner (at) lbl (dot) gov``` 







     