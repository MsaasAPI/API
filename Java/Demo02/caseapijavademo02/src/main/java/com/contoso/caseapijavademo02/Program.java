package com.contoso.caseapijavademo02;

public class Program 
{
    public static void main( String[] args )
    {
		// Gson gson = new GsonBuilder()
        //             .excludeFieldsWithoutExposeAnnotation()
        //             .create();

        System.out.println( "Hello World!" );

        // INote note = new Note();
        // note.setContent("test 1234");
        // // System.out.println(gson.toJson(note));

        // List<INote> notes = new ArrayList<INote>();
        // notes.add(note);

        // IContactBuilder contactBuilder = new ContactBuilder();
        // ICustomerBuilder customerBuilder = new CustomerBuilder();
        // IContact contact1 = (IContact)contactBuilder
        //                     .CreateContactOfLastName("LN1")
        //                     .OfFirstName("FN1")
        //                     .OfEmail("test1@g.co")
        //                     .OfPhone("1293847928734")
        //                     .Build();
                            
        // IContact contact2 = (IContact)contactBuilder
        //                     .CreateContactOfLastName("LN2")
        //                     .OfFirstName("FN2")
        //                     .OfEmail("test2@g.co")
        //                     .OfPhone("1293367728734")
        //                     .AsPrimaryContact()
        //                     .Build();

        // IContact contact3 = (IContact)contactBuilder
        //                     .CreateContactOfLastName("LN3")
        //                     .OfFirstName("FN3")
        //                     .OfEmail("test3@g.co")
        //                     .OfPhone("78747928734")
        //                     .Build();

        // List<IContact> newContacts = new ArrayList<IContact>();
        // newContacts.add(contact2);
        // newContacts.add(contact3);

        // ICustomer customer = (ICustomer)customerBuilder
        //                         .CreateCustomer()
        //                         .AddContactOfLastName("LN123")
        //                         .OfFirstName("FN456")
        //                         .OfEmail("E789")
        //                         .OfPhone("2983749834")
        //                         .OfPreferredContactChannel(ContactChannel.PHONE)
        //                         .BuildThisContact()
        //                         .AddContactOfLastName("LN00028")
        //                         .OfEmail("")
        //                         .OfPhone("fawoijaweoi")
        //                         .AsPrimaryContact()
        //                         .BuildThisContact()
        //                         .AddContactOfLastName("")
        //                         .OfFirstName("")
        //                         .OfEmail("")
        //                         .OfEmail("")
        //                         .OfPhone("")
        //                         .BuildThisContact()
        //                         .Build();

        // System.out.println(gson.toJson(customer));
        // // // System.out.println(gson.toJson(contacts));

        // // ICustomer customer = new Customer(contacts);


        // // IPartnerCaseReference pcr = new PartnerCaseReference("12345", "open", new PartnerAgentInformation("FNpai", "LNpai", "pai@pai.com", "123498729347"), "Contoso", "2387-23879-238729-238972387-34543");

        // // ICase newCase = new Case(customer, note, pcr);
        // // System.out.println(gson.toJson(newCase));
    }
}
