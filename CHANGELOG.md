# Changelog

## 1.12

OPD2515-specific temporary-root release.

### Runtime

- Locked the target to OPPO Pad Mini OPD2515 with kernel
  6.12.58-android16-6-g7704a1ae279b-ab15213644-4k.
- Added the bundled OPD2515 KernelSU userspace daemon, so a normal run needs
  only the installed APK and a single Run press.
- Replaced the two-press confirmation flow with a one-tap foreground launch.
- Disabled the boot receiver for this profile; root is started manually after
  each reboot and is removed by a full reboot.
- Forced soft reboot and module disabling off so the run does not mutate
  package-manager state.
- Waited for the KernelSU late-load worker before restoring SELinux enforcing and
  removing the temporary dfroot module.
- Kept OPPO guard probes audit-only. This release does not unload or freeze
  security components.

### Build and release

- Bumped the Android version to 1.12 / versionCode 32.
- Made the title use the generated application version.
- Updated CI action versions and release signing to use the configured
  DIRTYFRAG_KEYSTORE_B64 repository secret.
- Updated the OPD2515 custom ksud build to use the final application ID.
- Removed local kernel trees, logs, and generated LKM files from the source tree.

## 1.11

Historical upstream merge. This section describes the superseded 1.11 build and
is not the runtime contract of the OPD2515 1.12 profile above.

### Engine

- Merged the upstream DFRoot 4.0 and 4.1 engine changes.
- Added the soft-reboot path and the manager picker used by the older profile.
- Rebuilt the eight embedded kernel modules from the then-current LKM source.

### DirtyFrag

- Added per-run device information and a strictly linear progress display.
- Moved the build to AGP 9.4.0 with compile and target SDK 37.
- Added the advanced log and log sharing controls.

## 1.10

Based on upstream DFRoot 3.3.

- Moved the exploit to a staged executable and bootstrap handoff.
- Added runtime kallsyms resolution and CFI-safe kernel hooks.
- Added manager selection and the committed prebuilt KMI modules.

## 1.08

- Added the KernelSU Modules toggle.
- Improved status text and refresh behavior.

## Earlier

See the release notes on the releases page.
