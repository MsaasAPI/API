# MSaaS API Sample App for Postman.

| [Why Postman](#why-postman) | [Conceptual documentation](https://github.com/MsaasAPI/Postman/wiki) | [External References](#external-references)
| --- | --- | --- |

Postman is a popular API testing tool, and it is no exception for MSaaS API client implementation. Experience suggests that extra time investment in Postman can familiarize partner with the MSaaS API, flatten the learning curve, alleviate confusion, and facilitate trouble-shooting. It is our hope the readers find Postman helpful in the on-boarding process. 

## Why Postman
Although the overhead effort on Postman will not likely become part of the tangible product, it is well worth the time for the following reasons:
+ Quick prototyping: No heavy-duty IDE is needed. Just provision the parameters and leave the lower-level plumbing to Postman. Some lines of JavaScript will be needed only if advanced customization is desired.
+ Fault isolation: Decouple the client application from the trouble-shooting process, and therefore enable the user to pingpoint other issues such as the configuration and network.
+ Lower technical requirement: Although JavaScript literacy is advisible, it requires no programming knowledge to make basic API calls. This enable non-technical members (PM, analyst, agent, etc) to conduct preliminary support or testing.
+ Not but least, its UX is more friendly than intimidating comparing to other common competitors.

## License
Copyright (c) Microsoft Corporation.  All rights reserved. Licensed under the MIT License (the "License");

## We Value and Adhere to the Microsoft Open Source Code of Conduct
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## External References
+	[Postman Scripting](https://www.getpostman.com/docs/v6/postman/scripts/intro_to_scripts)
+	[Postman Dynamic Variables](https://www.getpostman.com/docs/v6/postman/environments_and_globals/variables#dynamic-variables)
+	[Postman Environment Variables](https://medium.com/@codebyjeff/using-postman-environment-variables-auth-token-ea9c4fe9d3d7)
+	[Postman Reusable Function Storage in Global Variable](https://github.com/postmanlabs/postman-app-support/issues/882#issuecomment-411299244)
+	[AAD Client Assertion (JWT) for Access (Bearer) Token Guide](https://docs.microsoft.com/en-us/azure/architecture/multitenant-identity/client-assertion)
+	[Client Assertion (JWT) for Access (Bearer) Token with Postman](https://medium.com/adobetech/using-postman-for-jwt-authentication-on-adobe-in-7573428ffe7f) 
+	[JWT Authentication with Postman](https://developer.sallinggroup.com/api-reference#guides-jwt-authentication-in-postman) 
+	[JsRsaSign Usage Tutorial](https://github.com/kjur/jsrsasign/wiki/Tutorial-for-JWS-generation)
