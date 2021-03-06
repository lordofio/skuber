package skuber.apps

import skuber.ext.extensionsAPIVersion
import skuber.{LabelSelector, ObjectMeta, ObjectResource, PersistentVolumeClaim, Pod}

/**
  * Created by hollinwilkins on 4/5/17.
  */
case class StatefulSet(override val kind: String ="StatefulSet",
                       override val apiVersion: String = extensionsAPIVersion,
                       metadata: ObjectMeta,
                       spec:  Option[StatefulSet.Spec] = None,
                       status:  Option[StatefulSet.Status] = None) extends ObjectResource {
  def withResourceVersion(version: String) = this.copy(metadata = metadata.copy(resourceVersion=version))

  lazy val copySpec = this.spec.getOrElse(new StatefulSet.Spec)

  def withReplicas(count: Int) = this.copy(spec=Some(copySpec.copy(replicas=count)))
  def withServiceName(serviceName: String) = this.copy(spec=Some(copySpec.copy(serviceName=Some(serviceName))))
  def withTemplate(template: Pod.Template.Spec) = this.copy(spec=Some(copySpec.copy(template=Some(template))))
  def withLabelSelector(sel: LabelSelector) = this.copy(spec=Some(copySpec.copy(selector=Some(sel))))

  def withVolumeClaimTemplate(claim: PersistentVolumeClaim) = {
    val spec = copySpec.withVolumeClaimTemplate(claim)
    this.copy(spec=Some(spec))
  }
}

object StatefulSet {
  def apply(name: String): StatefulSet =
    StatefulSet(metadata=ObjectMeta(name=name))

  case class Spec(replicas: Int = 1,
                  serviceName: Option[String] = None,
                  selector: Option[LabelSelector] = None,
                  template: Option[Pod.Template.Spec] = None,
                  volumeClaimTemplates: List[PersistentVolumeClaim] = Nil) {
    def withVolumeClaimTemplate(claim: PersistentVolumeClaim) = copy(volumeClaimTemplates = claim :: volumeClaimTemplates)
  }

  case class Status(observedGeneration: Int,
                    replicas: Int)
}
