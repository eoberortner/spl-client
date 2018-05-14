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
 
As a first step, you have to instantiate the BOOSTClient class using one of the following alternatives:

* providing your BOOST username and password.You can provide these in LoginCredentials class.

```
BOOSTClient client = new BOOSTClient(LoginCredentials.mUserName, LoginCredentials.mPassword);
```

* providing your BOOST JSON Web Token (JWT). You can provide your BOOST-JWT token in LoginCredentials class.

```
BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);
```

### Supported methods of the BOOST Client

* reverseTranslate

```
client.reverseTranslate(
	"./protein_sequences.fasta",    // a FASTA file containing the input sequences 
	Strategy.MostlyUsed,            // the codon selection strategy
	"./data/Ecoli.cudb",            // the codon usage table of the target host
	FileFormat.GENBANK);            // the desired output format
```

* codonJuggle

```
client.codonJuggle(
	"./dna_sequence.fasta",        // a FASTA file containing the input sequences 
	true,                          // are all sequences 5'-3' protein coding sequencese exclusively  
	Strategy.MostlyUsed,           // the codon replacement strategy
	"./data/Ecoli.cudb",           // the codon usage table of the target host
	FileFormat.GENBANK);           // the desired output format
```

* dnaVerification

```
client.dnaVarification(
	"./data/dna.fasta",           // input sequence
	"./data/constraints.scl",     // synthesis constraints
	"./data/patterns.fasta");     // sequence patterns
```
	
## Examples 

An example of invoking every supported method is provided in the [DemoClient](https://github.com/eoberortner/BOOST-REST-Client/blob/master/src/test/java/gov/doe/jgi/boost/client/DemoClient.java) 

## Questions, Feature Requests, or Bug-Report?

Please contact ```eoberortner (at) lbl (dot) gov``` 







     
