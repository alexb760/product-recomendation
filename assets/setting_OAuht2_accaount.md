
#Setting up an account and OAuth 2.0 client in Auth0

Perform the following steps to sign up for a free account in Auth0, configure both an OAuth 2.0 client and the product-composite API, and finally register a user:

    Open the URL, https://auth0.com, in your browser.
    Click on the SIGN UP button: 
        Sign up with an account of your choice.
        After a successful sign-up, you will be asked to create a tenant domain.
        Enter the name of the tenant of your choice, in my case: dev-ml.eu.auth0.com.
        Fill in information about your account as requested.

    Following sign-up, you will be directed to your dashboard. Select the Applications tab (on the left) to see the default client application that was created for you during the sign-up process.
    Click on the Default App to configure it:
        Copy the Client ID and Client Secret; you will need them later on.
        As Application Type, select Machine to Machine.
        As Token Endpoint Authentication Method, select POST.
        Enter http://my.redirect.uri as the allowed callback URL.
        Click on Show Advanced Settings, go to the Grant Types tab, deselect Client Credentials, and select the Password box.
        Click on SAVE CHANGES.
    Now define authorizations for our API:
        Click on the APIs tab (on the left) and click on the + CREATE API button.
        Name the API product-composite, give it the identifier https://localhost:8443/product-composite, and click on the CREATE button.
        Click on the Permissions tab and create two permissions (that is, OAuth scopes) for product:read and product:write.

    Next, create a user:
        Click on the Users & Roles and -> Users tab (on the left) and then on the + CREATE YOUR FIRST USER button.
        Enter an email and password of your preference and click on the SAVE button.
        Look for a verification mail from Auth0 in the Inbox for the email address you supplied.
    Finally, validate your Default Directory setting, used for the password grant flow:
        Click on your tenant profile in the upper-right corner and select Settings.
        In the tab named General, scroll down to the field named Default Directory and verify that it contains the Username-Password-Authentication value. If not, update the field and save the change.
    That's it! Note that both the default app and the API get a client ID and secret. We will use the client ID and secret for the default app; that is, the OAuth client. 

With an Auth0 account created and configured we can move on and apply the necessary configuration changes in the system landscape.
