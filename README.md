# BOOST REST API Client

The Build-OptimizatiOn Software Tools (BOOST) provide a REST API to access the following 
functionalities programmatically:
- Reverse-translation of protein sequences into DNA sequences
- Codon juggling of protein coding regions within DNA sequences
- Verification of DNA sequences against DNA synthesis constraints
- Modification of protein coding regions that violate DNA synthesis constraints
- Partitioning of DNA sequences into synthesizable building blocks

The data exchanged between a client and the BOOST REST API is represented in JavaScript Object Notation (JSON). 
After a successful login, every request from a client to the BOOST REST API requires a JSON Web Token (JWT) in 
the message header ([https://jwt.io/](https://jwt.io/)).

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


## Examples 

An example of invoking every supported method is provided in the DemoClient 

## Questions, Feature Requests, or Bug-Report?

Please contact ```eoberortner (at) lbl (dot) gov``` 







     