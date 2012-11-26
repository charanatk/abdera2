package org.apache.abdera2.examples.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Activity.ActivityBuilder;
import org.apache.abdera2.activities.model.Collection;
import org.apache.abdera2.activities.model.CollectionWriter;
import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.Verb;

import static org.apache.abdera2.activities.model.Activity.makeActivity;
import static org.apache.abdera2.activities.model.objects.PersonObject.makePerson;
import static org.apache.abdera2.activities.model.objects.ServiceObject.makeService;

/**
 * Demonstrates the use of the Generator and CollectionWriter 
 * utilities to produce activities using a template and stream
 * those out into an Activity Stream Collection. 
 */
public class GeneratorStreamingExample {

  public static void main(String... args) throws Exception {
    
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    
    // Prepare the writer
    IO io = IO.get();
    CollectionWriter writer = 
      io.getCollectionWriter(out, "UTF-8");
    
    // Output the collection header
    writer.writeHeader(
      ASBase.make()
        .set("title", "My Items")
        .set("totalItems", 10)
        .get());
    
    // Prepare the Activity template
    ActivityBuilder gen = 
      makeActivity()
      .actor(makePerson().get())
      .verb(Verb.POST)
      .provider(
        makeService()
          .displayName("My Application")
          .get())
      .displayName("joe");
    
    // we can now use the Generator to produce 
    // new Activities using the one we just 
    // created as a template. It's values will
    // be copied into the new object. Note, the 
    // copies are NOT deep.. that is, if you 
    // specify a List as one of the property
    // values in the template, each instance 
    // created will share a reference to that
    // same list object, so if the list if modified,
    // all copies generated will reference the 
    // modified list
    for (int n = 0; n < 10; n++)
      writer.writeObject(  // write out each object as we create it
        gen.template()
          .set("title", "A" + n)
          .get());
    
    // complete the writer.. very important.. always do this
    // or the json generated by the writer will be malformed
    writer.complete();
    
    // let's make sure we can read it
    ByteArrayInputStream in = 
      new ByteArrayInputStream(out.toByteArray());
    Collection<Activity> col = io.readCollection(in, "UTF-8");
    System.out.println(col.getProperty("title"));
    System.out.println(col.getTotalItems());
    for (Activity a : col.getItems())
      System.out.println(a.getTitle() + "\t" + a.getVerb());
  }
  
}