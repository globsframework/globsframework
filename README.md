# GlobsFramework

GlobsFramework is a metamodel framework designed to replace beans with a concept called a Glob. A Glob is essentially a
map-like object where field access keys are instances of a Field, rather than simple strings. These Fields, specific to
a given 'bean', are grouped within a GlobType, each containing additional information referred to as annotations (akin
to Java annotations, but in the form of a Globs ).

When working with a Glob, its associated GlobType is known, providing access to its fields. Real fields, such as
IntegerField, IntegerArrayField, DoubleField, GlobField, UnionField, and others, can be visited using the visitor
pattern. A Glob also retains information about whether a field has been set or not, with unset fields defaulting to null
upon access.

This framework enables writing generic code for serialization into XML/JSON/binary formats, database insertion,
self-comparison, and more, without relying on introspection. By utilizing GlobType to expose field access and known
field types, the written code gains full control. GlobsFramework treats GlobType as a pure data model, distinct from
classes used for encapsulating data (records). This simplifies writing code for tasks like serialization, database
access, etc., as it offers a straightforward interface compared to the complexities of class access via introspection.

Thus, while GlobsFramework finds utility in scenarios involving input/output operations, it also significantly enhances
code clarity and maintainability within applications where you manipulate data genericaly. Here are some typical use
cases:

* Managing data flow and configuration control in code.
* Handling user-input configurations.
* Implementing generic data filtering or transformation.

Additionally, there exists a JavaScript (TypeScript) version enabling dynamic screen creation for React configurations,
albeit with closed source code.

The main drawback of GlobsFramework is its limited compatibility with beans. However, its advantages include being
open-source, lightweight, dependency-free (except for slf4j for logging), and easy to maintain.

## history

The inspiration for GlobsFramework stems from the telecom industry's reliance on
GDMO (https://en.wikipedia.org/wiki/Guidelines_for_the_Definition_of_Managed_Objects) models, which were initially used
to generate code for various purposes like databases, UI, and ASN1 encoding. Over time, this generic model evolved into
direct use within codebases, with XML serving as the description format for Managed Objects (MO). A rewrite occurred for
a private financial company in 2006 by Regis Medina and Marc Guiot, and it continues to be actively used today.
Subsequently, an open-source version was developed for the BudgetView
project (https://web.archive.org/web/20181229134134/http://www.mybudgetview.com/, https://github.com/MarcGuiot/budgetview).
It is used in other company for aggregation of data and in an e-Commerce back-end.

## exemple of a db query in a lib that use Globs

```
sqlConnection.getQueryBuilder(DummyObject.TYPE,
                                and(Constraints.equal(DummyObject.NAME, "hello"),
                                        Constraints.equal(DummyObject.ID, 1)))
                        .selectAll()
                        .getQuery()
                        .executeAsGlobs();
```

## components

To view an example you can do a  ```git clone --recursive  https://github.com/globsframework/globs-allInOne.git``` and run code simplest/src/main/java/org/globsframework/sample/graphql/Example2.java

Today's Globs components :

* To access a database : https://github.com/globsframework/globs-db
* To read/write json : https://github.com/globsframework/globs-gson (depend on google gson)
* to read/write xml : https://github.com/globsframework/globs-xml (depends on https://github.com/globsframework/saxstack)
* to parse command line arguments : https://github.com/globsframework/globs-commandline
* to read/write csv or similar format : https://github.com/globsframework/csvExport (depend on apache csv)
* a binary serializer (a kind of TLV (Type Length Value) like protocol
  buffer) : https://github.com/globsframework/globs-bin-serialisation
* for http request using glob for params url, body, header (generate an openApi json)  with
  globs : https://github.com/globsframework/globs-http (depends on apache http components)
* to produce a view based on breakdown and output using data inside a glob or any where in it's
  child : https://github.com/globsframework/globs-view
* to implement a graphql api : https://github.com/globsframework/globs-graphql (no dependency except antlr graphql.g4, do not
  response to query on schema : I use graphql-java for that.)
* Glob can also be generated directly in bytecode via ASM https://github.com/globsframework/globs-generate
* a Protobuf serialization : 
* an offHeap api to tree (read-only) and hash (read-write) container
* a service using etcd but with globs : https://github.com/globsframework/globs-http (depends on jetcd)

A GlobType interface:

```
public interface GlobType {
    String getName();
    Field[] getFields();
    Field getField(String name) throws ItemNotFound;
    MutableGlob instantiate();
    Glob getAnnotation(Key key);

    ...
```

A Field interface

```
public interface Field {
   GlobType getGlobType()
   String getName();
   <T extends FieldVisitor> T accept(T visitor) throws Exception;
   Glob getAnnotation(Key key);
   ...
}

public interface StringField extends Field {
};

...
public interface GlobField extends Field {
  GlobType getTargetType();
};
...
```

A Glob interface:

```
public interface Glob {
    GlobType getType();
    boolean isSet(Field field) throws ItemNotFound;
    boolean isNull(Field field) throws ItemNotFound;
    Object getValue(Field field) throws ItemNotFound;
    Double get(DoubleField field) throws ItemNotFound;
    double get(DoubleField field, double valueIfNull) throws ItemNotFound;
    Optional<Double> getOpt(DoubleField field);
    Integer getValue(IntegerField field) throws ItemNotFound;
    ...
    Glob get(GlobField field) throws ItemNotFound;
    ...
    Glob[] get(GlobArrayField field) throws ItemNotFound;
    ...
```

A mutable Glob :

```
public insterface MutableGlob extends Glob {
   MutableGlob set(DoubleField field, Double value);
   void unset(Field field);
   ...
```

To create a GlobType (used in the json deserialization of a GlobType for exemple)
In these example, we create a GlobType, associate an 'annotation' called NamingField to a field, set and get a value for
the given field,
and retreive the field using the NamingFieldAnnotationType which is also a Glob.

```
         GlobType type = GlobTypeBuilderFactory.create("product")
            .addLongField("id")
            .addStringField("title", NamingFieldAnnotationType.INSTANCE)
            .addStringField("handle")
            .addDoubleField("price")
            .addBooleanField("published")
            .get();

        MutableGlob data = type.instantiate();

        StringField titleField = type.getTypedField("title");
        data.set(titleField, "XPhone");

        assertEquals("XPhone", data.get(titleField));

        Field namingField = data.getType().findFieldWithAnnotation(NamingFieldAnnotationType.UNIQUE_KEY);
        assertEquals("XPhone", data.getValue(namingField));
```

## static way when the type is known

```
public static class ProductType {
   public static GlobType TYPE;
   
   public static LongField id;

   public static StringField title;
   
   public static DoubleField price;
   
   public static BooleanField published;
   
   static {
      GlobTypeLoaderFactory.init(ProductType.class, "Product").load();
   }
}

MutableGlob data = ProductType.TYPE.instance();
data.set(ProductType.id, 43235)
    .set(ProductType.title, "XPhone")
    .set(ProductType.price, 1599.);

...

```

By leveraging both dynamic and static initialization, GlobsFramework offers flexibility and ease of use across various
scenarios.

The dynamic part for generic code :

```
Glob g = GSonUtils.decode("{'id': 43235, 'title': 'XPhone'}", ProductType.TYPE);
```

The static part when you know the attribut you want :

```
String title = g.get(ProductType.title)
assertEquals("XPhone", title);
```

