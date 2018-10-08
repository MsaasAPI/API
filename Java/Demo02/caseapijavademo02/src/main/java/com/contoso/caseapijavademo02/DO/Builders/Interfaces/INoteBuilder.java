package com.contoso.caseapijavademo02.DO.Builders.Interfaces;

import java.util.UUID;
import com.contoso.caseapijavademo02.DO.CaseAttributes.Interfaces.INote;

public interface INoteBuilder extends IEntityBuilder
{
    INoteBuilder addContent(String content);
	INote buildWithId(UUID id);

    @Override
    INote build();
}