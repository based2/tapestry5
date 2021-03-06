// Copyright 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry5.ioc.def;

import org.apache.tapestry5.ioc.Markable;

/**
 * Extended version of {@link org.apache.tapestry5.ioc.def.DecoratorDef} introduced to determine any
 * module method annotated with {@link org.apache.tapestry5.ioc.annotations.Decorate} as a decorator
 * method. Before version 5.2 a decorator was applied to any service whose id matched the pattern
 * provided
 * by {@link org.apache.tapestry5.ioc.annotations.Match} annotation. As of version 5.2 a service to
 * decorate may also be identified by a
 * combination of {@link org.apache.tapestry5.ioc.annotations.Advise} annotation and
 * a set of marker annotations.
 *
 * @since 5.2.2
 */
public interface DecoratorDef2 extends DecoratorDef, Markable
{

}
