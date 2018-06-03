package com.hendraanggrian.openpss.io

import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import org.apache.commons.lang3.SystemUtils.USER_HOME

object MainDirectory : Directory(USER_HOME, ".$ARTIFACT")