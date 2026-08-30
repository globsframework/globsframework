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

## Requirements and installation

Java 21. No runtime dependency but `slf4j-api`.

```xml
<dependency>
    <groupId>org.globsframework</groupId>
    <artifactId>globs</artifactId>
    <version>5.12.0</version>
</dependency>
```

The artifact is `globs`; the repository is `globsframework`.

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

Today's Globs components — the directory name is given when it differs from the artifact:

| Repository | Artifact | What it does |
| --- | --- | --- |
| [globs-db](https://github.com/globsframework/globs-db) | `globs-sql` | access a relational database from a `GlobType` |
| [globs-gson](https://github.com/globsframework/globs-gson) | `globs-gson` | read/write JSON, and `GlobType` ⇄ JSON (depends on google gson) |
| [globs-xml](https://github.com/globsframework/globs-xml) | `globs-xml` | read/write XML, over [saxstack](https://github.com/globsframework/saxstack) |
| [globs-csv](https://github.com/globsframework/globs-csv) | `globs-csv` | CSV, Excel and fixed-width files (depends on apache commons-csv and poi) |
| [globs-commandline](https://github.com/globsframework/globs-commandline) | `globs-commandline` | command-line arguments and environment variables as a Glob |
| [globs-bin-serialisation](https://github.com/globsframework/globs-bin-serialisation) | `globs-bin-serialisation` | a binary TLV serializer, protobuf-like, backward compatible |
| [globs-grpc](https://github.com/globsframework/globs-protobuf) | `globs-protobuf` | the protobuf wire format itself, without protobuf-java |
| [globs-http](https://github.com/globsframework/globs-http) | `globs-http` | an HTTP API whose url params, body and headers are Globs, with the OpenAPI document generated |
| [globs-graphql](https://github.com/globsframework/globs-graphql) | `globs-graphql` | a GraphQL engine (no dependency but the antlr grammar; schema introspection is left to graphql-java) |
| [globs-view](https://github.com/globsframework/globs-view) | `globs-view` | breakdown/output views over the data in a Glob or anywhere in its children |
| [globs-fix](https://github.com/globsframework/globs-fix) | `globs-fix` | a FIX 4.4 engine whose messages are Globs |
| [globs-network](https://github.com/globsframework/globs-rpc-direct) | `globs-rpc-direct` | RPC and streaming exchange over blocking sockets |
| [globs-etcd](https://github.com/globsframework/globs-etcd) | `globs-etcd` | publish and watch configuration in etcd (depends on jetcd) |
| [globs-shared](https://github.com/globsframework/globs-off-heap) | `globs-off-heap` | off-heap tree (read-only) and hash (read-write) containers |
| [globs-generate](https://github.com/globsframework/globs-generate) | `globs-generate` | Glob implementations and codec traversals generated in bytecode with ASM |
| [globs-mcp](https://github.com/globsframework/globs-mcp) | `globs-mcp` | `GlobType` → JSON Schema, and Model Context Protocol tools |

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

A Field interface (a sealed hierarchy : the permitted subtypes are exactly the known field kinds)

```
public sealed interface Field extends Annotations
        permits BooleanField, IntegerField, LongField, StringField, DoubleField, ... {
   GlobType getGlobType();
   String getName();
   DataType getDataType();
   int getIndex();
   <T extends FieldVisitor> T accept(T visitor) throws Exception;
   Glob getAnnotation(Key key);
   ...
}

public non-sealed interface StringField extends Field, Function<FieldValuesAccessor, String> {
};

...
public non-sealed interface GlobField extends Field, Function<FieldValuesAccessor, Glob> {
  GlobType getTargetType();
};
...
```

A Glob interface (the value accessors come from FieldValues / FieldValuesAccessor):

```
public interface Glob extends FieldValues {
    GlobType getType();
    Key getKey();
    MutableGlob duplicate();
    ...
}

public interface FieldValuesAccessor {
    boolean isSet(Field field) throws ItemNotFound;
    boolean isNull(Field field) throws ItemNotFound;
    Object getValue(Field field) throws ItemNotFound;
    Double get(DoubleField field) throws ItemNotFound;
    double get(DoubleField field, double valueIfNull) throws ItemNotFound;
    Optional<Double> getOpt(DoubleField field);
    Integer get(IntegerField field) throws ItemNotFound;
    ...
    Glob get(GlobField field) throws ItemNotFound;
    ...
    Glob[] get(GlobArrayField field) throws ItemNotFound;
    ...
```

A mutable Glob :

```
public interface MutableGlob extends Glob, FieldSetter<MutableGlob> {
   MutableGlob set(DoubleField field, Double value);   // from FieldSetter
   MutableGlob unset(Field field);
   ...
```

To create a GlobType (used in the json deserialization of a GlobType for exemple)
In these example, we create a GlobType, associate an 'annotation' called NamingField to a field, set and get a value for
the given field,
and retreive the field using the NamingField annotation, which is itself a Glob.

```
         GlobType type = GlobTypeBuilderFactory.create("product")
            .addLongField("id")
            .addStringField("title", NamingField.UNIQUE_GLOB)
            .addStringField("handle")
            .addDoubleField("price")
            .addBooleanField("published")
            .build();

        MutableGlob data = type.instantiate();

        StringField titleField = type.getTypedField("title");
        data.set(titleField, "XPhone");

        assertEquals("XPhone", data.get(titleField));

        Field namingField = data.getType().findFieldWithAnnotation(NamingField.KEY);
        assertEquals("XPhone", data.getValue(namingField));
```

## static way when the type is known

The same builder is used, but each field is *declared* : ```declareXxxField``` returns the typed Field instead of the
builder, so it can be assigned to a ```static final``` field of a holder class.

```
public static class ProductType {
   public static final GlobType TYPE;

   public static final LongField id;

   public static final StringField title;

   public static final DoubleField price;

   public static final BooleanField published;

   static {
      GlobTypeBuilder builder = GlobTypeBuilderFactory.create("Product");
      id = builder.declareLongField("id", KeyField.ZERO);
      title = builder.declareStringField("title", NamingField.UNIQUE_GLOB);
      price = builder.declareDoubleField("price");
      published = builder.declareBooleanField("published");
      TYPE = builder.build();
   }
}

MutableGlob data = ProductType.TYPE.instantiate();
data.set(ProductType.id, 43235L)
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


## Building

```bash
mvn test
mvn test -Dtest=GlobTypeBuilderTest          # one class
mvn test -Dtest=GlobTypeBuilderTest#test     # one method
mvn package                                  # also builds the test-jar the other globs-* repos depend on
```

Tests are JUnit 5. Surefire runs `*Test.java` / `*Tests.java` and skips `*TestCase.java`, which are shared
base classes.

## License

Apache License 2.0 — see <https://www.apache.org/licenses/LICENSE-2.0.txt>.

## Links

- [globsframework.org](https://globsframework.org)
- [GitHub organization](https://github.com/globsframework)
