authentication-context-ri
=========================

The reference implementation of the [authentication-context-api][1] based on 
[resource-api][2] and [property-manager-api][3].

#Component
The module contains one Declarative Services component. The component can be 
instantiated multiple times via Configuration Admin. The component registers 
the [AuthenticationPropagator][5] and the [AuthenticationContext][6] OSGi 
services.

#Concept
Full authentication concept is available on blog post [Everit Authentication][4].

[1]: https://github.com/everit-org/authentication-context-api
[2]: https://github.com/everit-org/resource-api
[3]: https://github.com/everit-org/property-manager-api
[4]: http://everitorg.wordpress.com/2014/07/31/everit-authentication/
[5]: http://attilakissit.wordpress.com/2014/07/09/everit-authentication/#authentication_propagator
[6]: http://attilakissit.wordpress.com/2014/07/09/everit-authentication/#authentication_context
