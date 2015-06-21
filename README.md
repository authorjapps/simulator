# Micro Services REST Simulator
## How to use the simulator?

### During unit testing / feature testing :
(Note: **See "maven-simulator-plugin" for Integration-Testing**)

e.g.
> While you are in need of an interfacing REST end point but it's not yet developed,
> then you can simulate this REST end point and do your Unit/Feature testing as below.

```````
    String endPoint = "/customers/1";
    String requiredResponse = "{" +
                "    \"id\": 1," +
                "    \"age\": 0," +
                "    \"isAdult\": false" +
                "}";

   RestApi api = new RestApi()
                .name("Get Customers By Id API")
                .operation(Method.GET)
                .url("/customers/1")
                .response(new RestResponse("{\"Locale\": \"en_gb\"}", 200, requiredResponse))
                .build();
                
    simulator = new SimpleRestSimulator(HTTP_PORT)
            .restApi(api)
            .run();
``````
> Now the REST end point "/customers/1" with method "GET" is ready to use.

* More examples here:
`````
- See: Test case: SimpleRestSimulatorTest
  #willSimulateGETAtRunTimeWithPortAndUrlUsingApiBuilder()
`````

> Sample log below after the above test was run:

````````
#Simulator: RESTFUL-simulator
#started. 
Listening at port: 9090 
2015-05-31 16:51:22,406 [Dispatcher: Thread-9] 561  [Dispatcher: Thread-9] INFO  o.j.s.impl.SimpleRestSimulator - 
#GET: Target URL: /customers/1 
2015-05-31 16:51:22,407 [Dispatcher: Thread-9] 562  [Dispatcher: Thread-9] INFO  o.j.s.impl.SimpleRestSimulator - 
# Response Status: 200 
2015-05-31 16:51:22,408 [Dispatcher: Thread-9] 563  [Dispatcher: Thread-9] INFO  o.j.s.impl.SimpleRestSimulator - 
# Response body: 
{
    "id": 1,
    "age": 0,
    "isAdult": false
} 
2015-05-31 16:51:22,507 [main] 662  [main] INFO  o.j.simulator.base.BaseSimulator - 
#RESTFUL-simulator
stopped.
````````

### To simulate more than one REST end points see:
````` 
        String endPoint1 = "/customers/1";
        String customerResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";

        String endPoint2 = "/orders/1";
        String orderResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"customerId\": 1,\n" +
                "    \"quantity\": 60\n" +
                "}";

        Api apiCustomer = new Api(
                "Get Customer By Id API",
                Method.GET,
                endPoint1,
                new RestResponse("some-headers", 200, customerResponse)
        );
        Api apiOrder = new Api(
                "Get Order Details By Order Id",
                Method.GET,
                endPoint2,
                new RestResponse(null, 200, orderResponse)
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(apiCustomer)
                .withApi(apiOrder)
                .run();
`````

### To run the predefined(in json files) simulator(s) as main.
`````
new SimpleRestJsonSimulatorsMain(PORT).start();
`````

### How it works?
Put your simulation Request URL and Response here in the respective json file or create a new file if not present.
- Path: src/main/resources/simulators
- e.g: simulators/customers-simulator.json
- Now to simulate this end-point: GET: http://localhost:9999/customers/1

Put the following JSON into the simulator json file above.
> Then run the "SimpleRestJsonSimulatorsMain"
> or new SimpleRestJsonSimulatorsMain(PORT).start(); 

> That's it. The REST api is ready to use.

Json structure is as below. Sample content of the **customers-simulator.json**
`````
{
  "name" : "Customers-Simulator",
  "apis" : [
    {
      "name": "Get Customer by Id",
      "operation": "GET",
      "url": "/customers/1",
      "response": {
        "status": 200,
        "body": {
          "id": 1,
          "name": "Kate",
          "sex": "Female"
        }
      }
    },
    {
      "name" : "Get Resources List",
      "operation" : "GET",
      "url" : "/customers",
      "response" : {
        "headers" : {
          "language" : "en_GB"
        },
        "status" : 200,
        "body" : [
          {
            "id": 1,
            "name": "Kate",
            "sex": "Female"
          },
          {
            "id": 2,
            "name": "Rowland",
            "sex": "Male"
          }
        ]
      }
    }
  ]
}
`````

Now:
Both end end points are ready to use. You can put as many end points as you need.
> Using your browser or REST client invoke: http://localhost:9999/customers/2 and see the response.
