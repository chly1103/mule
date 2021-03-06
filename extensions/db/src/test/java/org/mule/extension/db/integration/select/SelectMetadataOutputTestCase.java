/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.select;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.extension.db.internal.domain.metadata.SelectMetadataResolver.DUPLICATE_COLUMN_LABEL_ERROR;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.extension.db.integration.TestDbConfig;
import org.mule.extension.db.integration.model.AbstractTestDatabase;
import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.model.ArrayType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.runtime.api.metadata.descriptor.ComponentMetadataDescriptor;
import org.mule.runtime.api.metadata.resolving.FailureCode;
import org.mule.runtime.api.metadata.resolving.MetadataFailure;
import org.mule.runtime.api.metadata.resolving.MetadataResult;
import org.mule.runtime.core.api.registry.RegistrationException;
import org.mule.runtime.extension.api.introspection.declaration.type.ExtensionsTypeLoaderFactory;

import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runners.Parameterized;

public class SelectMetadataOutputTestCase extends AbstractDbIntegrationTestCase {

  private final ClassTypeLoader typeLoader = ExtensionsTypeLoaderFactory.getDefault().createTypeLoader();

  public SelectMetadataOutputTestCase(String dataSourceConfigResource, AbstractTestDatabase testDatabase) {
    super(dataSourceConfigResource, testDatabase);
  }

  @Parameterized.Parameters
  public static List<Object[]> parameters() {
    return TestDbConfig.getResources();
  }

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/select/select-metadata-config.xml"};
  }

  @Test
  public void selectAll() throws Exception {
    ObjectType record = getSelectOutputMetadata("select * from PLANET");

    assertThat(record.getFields().size(), equalTo(4));
    assertFieldOfType(record, "ID", testDatabase.getIdFieldMetaDataType());
    assertFieldOfType(record, "POSITION", testDatabase.getPositionFieldMetaDataType());
    assertFieldOfType(record, "NAME", typeBuilder.stringType().build());
    assertFieldOfType(record, "PICTURE", typeLoader.load(Blob.class));
  }

  @Test
  public void selectSome() throws Exception {
    ObjectType record = getSelectOutputMetadata("select ID, POSITION from PLANET");

    assertThat(record.getFields().size(), equalTo(2));
    assertFieldOfType(record, "ID", testDatabase.getIdFieldMetaDataType());
    assertFieldOfType(record, "POSITION", testDatabase.getPositionFieldMetaDataType());
  }

  @Test
  public void selectJoin() throws Exception {
    ObjectType record = getSelectOutputMetadata("select NAME, NAME as NAME2 from PLANET");

    assertThat(record.getFields().size(), equalTo(2));
    assertFieldOfType(record, "NAME", typeBuilder.stringType().build());
    assertFieldOfType(record, "NAME2", typeBuilder.stringType().build());
  }

  @Test
  public void selectInvalidJoin() throws Exception {
    MetadataResult<ComponentMetadataDescriptor> metadata = getMetadata("selectMetadata", "select NAME, NAME from PLANET");

    assertThat(metadata.isSuccess(), is(false));
    assertThat(metadata.get().getOutputMetadata().isSuccess(), is(false));
    Optional<MetadataFailure> failure = metadata.get().getOutputMetadata().getFailure();
    assertThat(failure.isPresent(), is(true));
    assertThat(failure.get().getFailureCode(), is(FailureCode.INVALID_METADATA_KEY));
    assertThat(failure.get().getMessage(), is(DUPLICATE_COLUMN_LABEL_ERROR));
  }

  private ObjectType getSelectOutputMetadata(String query) throws RegistrationException {
    MetadataResult<ComponentMetadataDescriptor> metadata = getMetadata("selectMetadata", query);

    assertThat(metadata.isSuccess(), is(true));
    assertThat(metadata.get().getOutputMetadata().isSuccess(), is(true));
    assertThat(metadata.get().getOutputMetadata().get().getPayloadMetadata().isSuccess(), is(true));
    ArrayType output = (ArrayType) metadata.get().getOutputMetadata().get().getPayloadMetadata().get().getType();
    return (ObjectType) output.getType();
  }

}
