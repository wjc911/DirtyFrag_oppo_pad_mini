# DirtyFrag for OPPO Pad Mini (OPD2515)

This repository contains the OPD2515-specific DirtyFrag build used for temporary,
user-launched root on the OPPO Pad Mini.

The shipped profile is locked to:

- **Model:** OPD2515
- **Kernel:** 6.12.58-android16-6-g7704a1ae279b-ab15213644-4k
- **Application ID:** local.opd2515.dirtyfrag908
- **Release:** 1.12

The exploit is device and kernel specific. A system update can change the kernel
or its mitigation state, so the target check is intentionally strict.

## Runtime behavior

1. Install the APK from the [latest release](https://github.com/wjc911/DirtyFrag_oppo_pad_mini/releases/latest).
2. Open **DirtyFrag** and press **Run** once.
3. The APK carries the OPD2515 KernelSU userspace daemon, so normal operation does
   not require ADB or a second manager app.
4. Root is temporary. A full reboot removes the loaded dfroot and KernelSU
   modules; the APK must be launched again after that reboot.

The boot receiver is disabled in this profile. Soft reboot and automatic module
disabling are also forced off so the run does not change package-manager state.
The bootstrap restores SELinux enforcing after the KernelSU late-load handoff.
The kernel module records OPPO guard activity for diagnostics; it does not unload
or freeze those components.

If the model or complete kernel release does not match the values above, the APK
stops before attempting the exploit.

## What is shipped

- The exact Android 16 / 6.12 dfroot.ko for the OPD2515 GKI.
- A bundled libksud.so userspace daemon.
- A one-tap foreground flow with no boot-time autorun.
- Device-protected preferences so a reboot starts with a clean run state.
- Audit-only guard probes and explicit cleanup of the temporary dfroot module.

An installed app that carries a compatible libksud.so can still be selected from
the SU Manager card for development experiments, but it is not required by the
released OPD2515 profile.

## Building locally

~~~sh
JAVA_HOME="<Android Studio jbr>" ./gradlew --no-daemon :app:assembleRelease
~~~

The APK is written to app/build/outputs/apk/release/dirtyfrag.apk.

Release builds use app/keystore.jks, which is ignored by Git. Generate a local
test key with:

~~~sh
keytool -genkeypair -v -keystore app/keystore.jks -alias dirtyfrag   -keyalg RSA -keysize 2048 -validity 10000   -storepass dirtyfrag -keypass dirtyfrag
~~~

The eight prebuilt modules under app/src/main/jni/ko/ are tracked inputs for a
plain clone. The CI Build workflow also rebuilds every KMI module from
lkm/dfroot.c. The OPD2515 workflow additionally rebuilds the exact module
against the pinned Android common kernel and GKI symbol table.

## CI and releases

- Build runs on pushes and pull requests to main, and on v* tags.
- Build OPD2515 exact candidate runs on pushes to main and can be dispatched
  manually.
- A v* tag creates a signed release APK from the tag's source and extracts its
  notes from CHANGELOG.md.
- Release signing uses the repository secret DIRTYFRAG_KEYSTORE_B64; the private
  keystore is never committed.

## Technical outline

DirtyFrag uses the DirtyFrag page-cache write primitive to patch a privileged
libc++ call path and load the matching kernel module. The module starts the
app-private bootstrap through the vendor modprobe path. The bootstrap stages the
bundled ksud, waits for the late-load worker to finish, restores SELinux
enforcing, and removes the temporary dfroot module. Nothing in this profile
writes a boot image or enables a boot-time root path.

## Credits

- Upstream project and exploit chain: https://github.com/diabl0w/DFRoot
- Original PoC and various code: https://github.com/lsposed/lspromise
- SELinux permissive modules and related code: https://github.com/polygraphene/DFReroot
- Unprivileged XFRM socket method: https://github.com/combeng6th/DirtyInit
