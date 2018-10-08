package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.INote;

/**
 * Contact for builder to instantiate Note in fluent manner.
 */
public interface INoteBuilder extends IEntityBuilder
{
    INoteBuilder addContent(String content);
	INote buildWithId(UUID id);

    @Override
    INote build();
}