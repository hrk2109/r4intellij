/*
 * Copyright 2011-2012 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r4intellij.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;


/**
 * Allow for language injection into string literals
 *
 * @author gregsh
 * @author brandl http://devnet.jetbrains.net/message/5448773#5448773
 */
public class RStringManipulator extends AbstractElementManipulator<RStringInjectHost> {

    @Override
    public RStringInjectHost handleContentChange(RStringInjectHost psi, TextRange range, String newContent) throws IncorrectOperationException {
        final String oldText = psi.getText();
        final String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        return psi.updateText(newText);
    }


    @Override
    public TextRange getRangeInElement(final RStringInjectHost element) {
        return getStringTokenRange(element);
    }


    public static TextRange getStringTokenRange(final RStringInjectHost element) {
        return TextRange.from(1, element.getTextLength() - 2);
    }
}
