package io.tolgee.development.testDataBuilder.builders

import io.tolgee.development.testDataBuilder.FT
import io.tolgee.model.ApiKey
import io.tolgee.model.AutoTranslationConfig
import io.tolgee.model.Language
import io.tolgee.model.Organization
import io.tolgee.model.Permission
import io.tolgee.model.Project
import io.tolgee.model.Screenshot
import io.tolgee.model.dataImport.Import
import io.tolgee.model.key.Key
import io.tolgee.model.key.Namespace
import io.tolgee.model.key.screenshotReference.KeyScreenshotReference
import io.tolgee.model.keyBigMeta.KeysDistance
import io.tolgee.model.mtServiceConfig.MtServiceConfig
import io.tolgee.model.translation.Translation
import org.springframework.core.io.ClassPathResource

class ProjectBuilder(
  organizationOwner: Organization? = null,
  val testDataBuilder: TestDataBuilder
) : BaseEntityDataBuilder<Project, ProjectBuilder>() {
  override var self: Project = Project().apply {
    if (organizationOwner == null) {
      if (testDataBuilder.data.organizations.size > 0) {
        this.organizationOwner = testDataBuilder.data.organizations.first().self
      }
      return@apply
    }
    this.organizationOwner = organizationOwner
  }

  class DATA {
    val permissions = mutableListOf<PermissionBuilder>()
    val languages = mutableListOf<LanguageBuilder>()
    val imports = mutableListOf<ImportBuilder>()
    val keys = mutableListOf<KeyBuilder>()
    val translations = mutableListOf<TranslationBuilder>()
    val apiKeys = mutableListOf<ApiKeyBuilder>()
    val translationServiceConfigs = mutableListOf<MtServiceConfigBuilder>()
    var autoTranslationConfigBuilder: AutoTranslationConfigBuilder? = null
    var avatarFile: ClassPathResource? = null
    var namespaces = mutableListOf<NamespaceBuilder>()
    var keyScreenshotReferences = mutableListOf<KeyScreenshotReferenceBuilder>()
    var screenshots = mutableListOf<ScreenshotBuilder>()
    var keyDistances = mutableListOf<KeysDistanceBuilder>()
  }

  var data = DATA()

  fun addPermission(ft: FT<Permission>) = addOperation(data.permissions, ft)

  fun addApiKey(ft: FT<ApiKey>) = addOperation(data.apiKeys, ft)

  fun addImport(ft: FT<Import> = {}) =
    addOperation(data.imports, ImportBuilder(this), ft)

  fun addLanguage(ft: FT<Language>) =
    addOperation(data.languages, ft)

  fun addKey(ft: FT<Key>) = addOperation(data.keys, ft)

  fun addKey(namespace: String? = null, keyName: String, ft: (KeyBuilder.() -> Unit)? = null): KeyBuilder {
    return addKey(keyName, ft).build { setNamespace(namespace) }
  }

  fun addKey(keyName: String, ft: (KeyBuilder.() -> Unit)?): KeyBuilder {
    return addKey {
      name = keyName
    }.apply {
      ft?.let {
        apply(it)
      }
    }
  }

  fun addTranslation(ft: FT<Translation>) = addOperation(data.translations, ft)

  fun addMtServiceConfig(ft: FT<MtServiceConfig>) =
    addOperation(data.translationServiceConfigs, ft)

  fun setAvatar(filePath: String) {
    data.avatarFile = ClassPathResource(filePath, this.javaClass.classLoader)
  }

  fun addAutoTranslationConfig(ft: FT<AutoTranslationConfig>) {
    data.autoTranslationConfigBuilder = AutoTranslationConfigBuilder(this@ProjectBuilder).also { ft(it.self) }
  }

  fun addNamespace(ft: FT<Namespace>) = addOperation(data.namespaces, ft)

  fun addScreenshotReference(ft: FT<KeyScreenshotReference>) = addOperation(data.keyScreenshotReferences, ft)

  fun addScreenshot(ft: FT<Screenshot>) = addOperation(data.screenshots, ft)

  fun addEnglish(): LanguageBuilder {
    return addLanguage {
      name = "English"
      tag = "en"
    }
  }

  fun addGerman(): LanguageBuilder {
    return addLanguage {
      name = "German"
      originalName = "Deutsch"
      tag = "de"
    }
  }

  fun addCzech(): LanguageBuilder {
    return addLanguage {
      name = "Czech"
      originalName = "Čeština"
      tag = "cs"
    }
  }

  fun addFrench(): LanguageBuilder {
    return addLanguage {
      name = "French"
      originalName = "Français"
      tag = "fr"
    }
  }

  fun getLanguageByTag(tag: String): LanguageBuilder? {
    return data.languages.find { it.self.tag == tag }
  }

  fun addKeysDistance(key1: Key, key2: Key, ft: FT<KeysDistance>): KeysDistanceBuilder {
    val builder = KeysDistanceBuilder(this, key1, key2)
    ft(builder.self)
    data.keyDistances.add(builder)
    return builder
  }

  val onlyUser get() = this.self.organizationOwner.memberRoles.singleOrNull()?.user
}
