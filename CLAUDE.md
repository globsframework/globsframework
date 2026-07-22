# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and test

Java 21, Maven, no runtime dependency except slf4j-api. Everything else (junit-jupiter, log4j, asm, xerces, kryo5, jmh, saxstack) is test scope.

```bash
mvn compile
mvn test
mvn test -Dtest=GlobTypeBuilderTest          # single test class
mvn test -Dtest=GlobTypeBuilderTest#test     # single method
mvn package                                  # also builds a test-jar (published, other globs-* repos depend on it)
```

Dependencies resolve from GitHub Packages (`maven.pkg.github.com/globsframework/*`); CI passes `-s settings.xml` with `GH_MAVEN_REGISTRY_USER` / `GH_MAVEN_REGISTRY_ACCESS_TOKEN`. Locally, `mvn -o` works once the cache is warm.

Surefire runs `**/*Test.java` and `**/*Tests.java`, and explicitly excludes `**/*TestCase.java` (those are shared base classes). Tests are JUnit 5 (`org.junit.jupiter`).

Releases go through `maven-release-plugin` (`pom.xml.releaseBackup` / `release.properties` in the working tree are leftovers of a release run, not source).

## What this is

A metamodel framework: instead of beans, data lives in a `Glob` — a map-like object whose keys are `Field` instances belonging to a `GlobType`. Generic code (serialization, DB access, HTTP, GraphQL — all in sibling repos) is written against `GlobType`/`Field`/visitors rather than reflection. This repo is only the core; see README for the component list.

Everything lives under `org.globsframework.core`.

## Architecture

**`metamodel/` — the type side.**
`GlobType` holds an ordered `Field[]`, key fields, indices, annotations, and a `GlobFactory`. Fields are looked up by name or by `getIndex()` (declaration order — the index is what all the fast paths use). `GlobType.getRegistered(Class)` is a per-type extension slot that libraries use to attach behavior.

Types are built with `GlobTypeBuilderFactory.create(name)`. The builder has two parallel families:
- `addXxxField(name, annotations...)` — fluent, returns the builder; use when the type is built dynamically.
- `declareXxxField(name, annotations...)` — returns the typed `Field`; use when assigning into `static final` fields of a Java holder class.

`GlobTypeBuilder.build()` closes the type (the README's `.get()` is stale too). Nested/recursive types are handled by passing `Supplier<GlobType>` to `addGlobField`/`addGlobArrayField`.

Note: the README still shows `GlobTypeLoaderFactory.init(Class, name).load()`. That reflective loader no longer exists — the builder API above replaced it. Follow `src/test/java/org/globsframework/core/metamodel/DummyObject.java` for the current static-holder idiom.

**Annotations are Globs, and come in pairs.** For each annotation there are two files in `metamodel/annotations/`:
- `Foo.java` — a real `GlobType` (`Foo.TYPE`), usually with `UNIQUE_KEY` (a `Key` used to look the annotation up) and, for valueless annotations, `UNIQUE_GLOB`/`INSTANCE`.
- `Foo_.java` — a Java `@interface` carrying `GlobType TYPE = Foo.TYPE`, so the annotation can also be written as a real Java annotation on a field.

The bridge is `typeBuilder.register(GlobCreateFromAnnotation.class, annotation -> ...)` inside `Foo`'s static block: it converts a Java annotation instance into the annotation Glob. Core itself never reflects over Java annotations (nothing in `src/main` calls `getDeclaredFields`/`getDeclaredAnnotations`) — that conversion exists for external tooling such as `globs-generate`. Inside this repo an annotation takes effect **only** when its Glob is passed to `declareXxxField`/`addXxxField`, so don't add a `@Foo_` marker to a holder class expecting it to do anything (the inert ones the test types used to carry have been removed). When adding an annotation, follow the pair exactly and list it in `AllCoreAnnotations`. Annotations are then queried with `field.findAnnotation(Foo.UNIQUE_KEY)` or `type.getFieldWithAnnotation(...)`.

**`Field` is a sealed interface** (`metamodel/fields/Field.java`) permitting exactly the concrete field kinds — scalar, array, `GlobField`, `GlobUnionField`, and their array variants. Adding a field type is invasive: the `permits` clause, `DataType`, `metamodel/fields/impl/`, every `FieldVisitor` / `FieldValueVisitor` variant (plus the `AbstractFieldVisitor` / `AbstractWithErrorVisitor` inner classes), `DefaultFieldFactory`, `DefaultGlobTypeBuilder`, `GlobTypeToGlob`, and the serializers. Visitors come in `accept`/`safeAccept` pairs (checked vs. wrapped exceptions) and with 0/1/2 context parameters — keep all variants in sync.

**`metamodel/type/` is the metamodel of the metamodel**: `GlobTypeType`, `StringFieldType`, etc. are GlobTypes describing GlobTypes, which is how `utils/GlobTypeToGlob` round-trips a `GlobType` into Globs so type definitions themselves can be serialized.

**`model/` — the instance side.**
`Glob extends FieldValues`, `MutableGlob` adds setters. Read accessors are overloaded per field type: `get(DoubleField)` returns `Double`, `get(DoubleField, double)` gives a null-default, `getOpt` gives `Optional`, `getValue(Field)` is the untyped path. `isSet` vs `isNull` are distinct — an unset field reads as null.

`model/impl/` has `DefaultGlob32/64/128/DefaultGlob`, differing only in how the "is set" bitset is stored (int / long / two longs / array). `DefaultGlobFactory` picks one from the field count, floored by `-Dgfw.minSize` (default 64). Keys are similarly specialized: `EmptyKey`, `SingleFieldKey`, `TwoFieldKey`, `ThreeFieldKey`, `FourFieldKey`, `CompositeKey`. This specialization is deliberate — allocation size and hashing cost matter here; don't collapse the variants.

`GlobRepository` (`model/repository/`) is an in-memory store keyed by `Key`, with index support (`metamodel/index/`, `model/indexing/`), links (`metamodel/links/`), and change tracking: mutations produce a `ChangeSet` of `DeltaGlob`s (`model/delta/`) delivered to `ChangeSetListener`s. `LocalGlobRepository` gives a transactional local view over another repository.

`functional/` — `FunctionalKey` is a key over an arbitrary field subset (not the type's key fields), used for joins/lookups. Its hash intentionally uses `GlobType.getName()` rather than the type's identity hash, so hashes stay stable across JVMs.

**`utils/serialization/`** — hand-rolled binary format. `SerializedInput`/`SerializedOutput` with `Default*` (streams), `ByteBuffer*` / `NByteBuffer*` (ByteBuffer-backed), `Compressed*`, and `*Checker` decorators; `GlobSerializer`/`GlobDeSerializer` do the Glob-level encoding. These classes are `final` and written for throughput — changes here need the serialization tests to pass round-trip in both directions.

**`utils/container/`** — size-specialized `GlobContainer` implementations (`OneElementContainer`, `TwoElementContainer`, `ArrayContainer`, `HashGlobContainer`, plus `intkey/`, `hash/`, `specific/`) used by the repository and indices. Same principle as the Glob variants: pick the cheapest representation for the actual cardinality.

**`directory/`** — a small service locator (`Directory`) used to pass services around; unrelated to the data model.

## Conventions

- Assertions (`assert`) guard field/type consistency in hot paths so checks vanish in production; keep new checks in that style rather than adding unconditional branches.
- `-Dglobs.builder=<class>` swaps in an alternative `GlobFactoryService` (that is how the ASM-based `globs-generate` plugs in bytecode-generated Globs). Any change to `GlobFactory`/`GlobType` must remain implementable by an external factory.
- Tests build their model types as `Dummy*` classes in `src/test/java/org/globsframework/core/metamodel/`; reuse those instead of defining new one-off types. `GlobChecker`, `GlobRepositoryChecker`, `TestUtils` are the shared assertion helpers.
- `src/test/java/org/globsframework/core/xml/` holds test-only XML parsing/writing (the real XML component is a separate repo).
